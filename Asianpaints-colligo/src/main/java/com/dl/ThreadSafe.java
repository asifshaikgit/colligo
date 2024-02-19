package com.dl;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.execution.spi.Execution;
import com.apigee.flow.message.MessageContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONObject;

public class ThreadSafe implements Execution {
	
	@Override
	public ExecutionResult execute(MessageContext mc, ExecutionContext ec) {
		long startTime = System.currentTimeMillis();
		Router crc = new Router(mc, ec);

		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<ExecutionResult> futureResult = service.submit(crc);
		try {
			// Put timeout limit based on your app
			ExecutionResult result = futureResult.get(30000, TimeUnit.MILLISECONDS);
			return result;
		} catch (TimeoutException timeout) {
			try {
				service.shutdownNow();
				JSONObject mainJson = new JSONObject();
				mainJson.put("SUCCESS", "0");
				mainJson.put("RESPONSE_MESSAGE", "Timeout Occured !");
				mainJson.put("EXECUTION_TIME", "" + (System.currentTimeMillis() - startTime));
				mc.getMessage().setContent(mainJson.toString());
			} catch (Exception exp) {
				exp.printStackTrace();
			}
			return ExecutionResult.ABORT;
		} catch (Exception expc) {
			try {
				service.shutdownNow();
				JSONObject mainJson = new JSONObject();
				mainJson.put("SUCCESS", "0");
				mainJson.put("RESPONSE_MESSAGE", "Error : " + expc.getMessage());
				mainJson.put("EXECUTION_TIME", "" + (System.currentTimeMillis() - startTime));
				mc.getMessage().setContent(mainJson.toString());
			} catch (Exception exp) {
				exp.printStackTrace();
			}
			return ExecutionResult.ABORT;
		} finally {
			try {
				service.shutdownNow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
