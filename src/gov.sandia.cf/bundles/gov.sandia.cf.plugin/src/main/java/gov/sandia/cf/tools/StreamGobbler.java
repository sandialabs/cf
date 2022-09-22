/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * A stream gobbler implementing runnable (used by processes).
 * 
 * @author Didier Verstraete
 *
 */
public class StreamGobbler implements Runnable {

	/** The input stream. */
	private InputStream inputStream;
	private Consumer<String> consumer;

	/**
	 * Instantiates a new stream gobbler.
	 *
	 * @param inputStream the input stream
	 * @param consumer    the consumer
	 */
	public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
		this.inputStream = inputStream;
		this.consumer = consumer;
	}

	@Override
	public void run() {
		new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
	}

}
