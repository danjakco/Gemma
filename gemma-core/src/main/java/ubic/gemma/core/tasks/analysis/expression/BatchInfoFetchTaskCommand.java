package ubic.gemma.core.tasks.analysis.expression;

import ubic.gemma.core.job.TaskCommand;
import ubic.gemma.core.job.TaskResult;
import ubic.gemma.core.tasks.Task;
import ubic.gemma.core.tasks.maintenance.ExpressionExperimentReportTaskCommand;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 *
 *
 */
public class BatchInfoFetchTaskCommand extends ExpressionExperimentReportTaskCommand {

    private static final long serialVersionUID = -1901958943061377082L;

    public BatchInfoFetchTaskCommand( ExpressionExperiment expressionExperiment ) {
        super( expressionExperiment );
        this.setMaxRuntime( 15 );
    }

    @Override
    public Class<? extends Task<TaskResult, ? extends TaskCommand>> getTaskClass() {
        return BatchInfoFetchTask.class;
    }
}
