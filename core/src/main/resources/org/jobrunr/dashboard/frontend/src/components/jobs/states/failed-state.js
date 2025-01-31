import React from "react";
import Accordion from "@material-ui/core/Accordion";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import ExpandMore from "@material-ui/icons/ExpandMore";
import Alert from "@material-ui/lab/Alert";
import Typography from "@material-ui/core/Typography";
import {makeStyles} from "@material-ui/core/styles";
import TimeAgo from "react-timeago/lib";

const useStyles = makeStyles(theme => ({
    primaryHeading: {
        textTransform: "none",
        lineHeight: "inherit"
    },
    secondaryHeading: {
        alignSelf: 'center',
        marginLeft: 'auto'
    },
    alert: {
        padding: 0
    },
    failed: {
        color: "rgb(97, 26, 21)",
        backgroundColor: "rgb(253, 236, 234)",
        minHeight: 56
    },
    expansionPanel: {
        display: "block"
    },
    exceptionClass: {
        textTransform: "none"
    },
    stackTrace: {
        whiteSpace: "pre-wrap",
        wordWrap: "break-word"
    }
}));


const Failed = (props) => {
    const classes = useStyles();
    const jobState = props.jobState;

    return (
        <Accordion className={classes.root}>
            <AccordionSummary
                className={classes.failed}
                id="failed-panel-header"
                expandIcon={<ExpandMore/>}
                aria-controls="failed-panel-content"
            >
                <Alert className={classes.alert} severity="error">
                    <Typography className={classes.primaryHeading} variant="h6">
                        Job processing failed - {jobState.message}
                    </Typography>
                </Alert>
                <Typography className={classes.secondaryHeading}>
                    <TimeAgo date={new Date(jobState.createdAt)} title={new Date(jobState.createdAt).toString()}/>
                </Typography>
            </AccordionSummary>

            <AccordionDetails className={classes.expansionPanel}>
                <Typography variant="h6" className={classes.exceptionClass} gutterBottom>
                    {jobState.exceptionType}
                </Typography>
                <Typography component={'pre'} className={classes.stackTrace}>
                    {jobState.stackTrace}
                </Typography>
            </AccordionDetails>
        </Accordion>
    )
};

export default Failed;