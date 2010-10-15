package es.caib.mobtratel.admin.scheduler.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class MobtratelJobListener implements JobListener
{
	Log log = LogFactory.getLog( MobtratelJobListener.class );
	
	public String getName()
	{
		return "Job Listener";
	}

	public void jobToBeExecuted(JobExecutionContext arg0)
	{
		log.debug( "Job to be executed" );

	}

	public void jobExecutionVetoed(JobExecutionContext arg0)
	{
		log.debug( "Job execution vetoed" );

	}

	public void jobWasExecuted(JobExecutionContext arg0,
			JobExecutionException exception)
	{
		
		log.debug ( "Job executed at time " + new java.util.Date() );
	
		if (exception != null) {
			log.error("Job error");
			exception.printStackTrace();
			log.error( "Executing job " + arg0.getJobDetail().getName(), exception );			
		}

	}

}
