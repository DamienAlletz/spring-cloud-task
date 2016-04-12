/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.task.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.cloud.task.batch.listener.support.StepExecutionEvent;
import org.springframework.cloud.task.batch.listener.support.MessagePublisher;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

/**
 * Setups up the StepExecutionListener to emit events to the spring cloud stream output channel.
 *
 * @author Michael Minella
 * @author Glenn Renfro
 */
public class EventEmittingStepExecutionListener implements StepExecutionListener {

	private MessageChannel output;

	private MessagePublisher<StepExecutionEvent> messagePublisher;

	public EventEmittingStepExecutionListener(MessageChannel output) {
		Assert.notNull(output, "An output channel is required");
		this.output = output;
		this.messagePublisher = new MessagePublisher<>(output);
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.messagePublisher.publish(new StepExecutionEvent(stepExecution));
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		this.messagePublisher.publish(new StepExecutionEvent(stepExecution));

		return stepExecution.getExitStatus();
	}
}