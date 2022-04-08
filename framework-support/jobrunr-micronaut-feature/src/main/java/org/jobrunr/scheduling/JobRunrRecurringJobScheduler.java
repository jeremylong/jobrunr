package org.jobrunr.scheduling;

import io.micronaut.inject.ExecutableMethod;
import org.jobrunr.jobs.JobDetails;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.micronaut.annotations.Recurring;
import org.jobrunr.scheduling.cron.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.ZoneId;

import static java.util.Collections.emptyList;

public class JobRunrRecurringJobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRunrRecurringJobScheduler.class);

    private final JobScheduler jobScheduler;

    public JobRunrRecurringJobScheduler(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    public void schedule(ExecutableMethod<?, ?> method) {
        Method call =  method.getTargetMethod();
        if (call.getParameterCount() > 0 && !(call.getParameterCount() == 1 && JobContext.class.isAssignableFrom(call.getParameterTypes()[0]))) {
            throw new IllegalStateException("Methods annotated with " + Recurring.class.getName() + " can only have zero parameters or a single parameter of type JobContext.");
        }

        String id = getId(method);
        String cron = getCron(method);

        if (Recurring.CRON_DISABLED.equals(cron)) {
            if (id == null) {
                LOGGER.warn("You are trying to disable a recurring job using placeholders but did not define an id.");
            } else {
                jobScheduler.delete(id);
            }
        } else {
            JobDetails jobDetails = getJobDetails(method);
            ZoneId zoneId = getZoneId(method);
            jobScheduler.scheduleRecurrently(id, jobDetails, CronExpression.create(cron), zoneId);
        }
    }

    private String getId(ExecutableMethod<?, ?> method) {
        return method.stringValue(Recurring.class, "id").orElse(null);
    }

    private String getCron(ExecutableMethod<?, ?> method) {
        return method.stringValue(Recurring.class, "cron").orElseThrow(() -> new IllegalArgumentException("Cron attribute is required"));
    }

    private JobDetails getJobDetails(ExecutableMethod<?, ?> method) {
        final JobDetails jobDetails = new JobDetails(method.getTargetMethod().getDeclaringClass().getName(), null, method.getTargetMethod().getName(), emptyList());
        jobDetails.setCacheable(true);
        return jobDetails;
    }

    private ZoneId getZoneId(ExecutableMethod<?, ?> method) {
        return method.stringValue(Recurring.class, "zone").map(ZoneId::of).orElse(ZoneId.systemDefault());
    }
}
