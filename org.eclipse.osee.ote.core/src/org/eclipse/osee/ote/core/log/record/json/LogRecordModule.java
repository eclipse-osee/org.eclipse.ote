/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ote.core.log.record.json;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class LogRecordModule extends SimpleModule {

	public LogRecordModule() {
		super("LogRecordModule", new Version(0, 0, 1, "", "", ""));
	}

	@Override
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(LogRecord.class, MixIn.class);
	}

	abstract class MixIn {
		MixIn(@JsonProperty("Level") Level level, @JsonProperty("Message") String message) {
		   // INTENTIONALLY EMPTY BLOCK
		};

		@JsonProperty
		abstract public Level getLevel();
		
		@JsonProperty
		abstract public String getMessage();
	}
}
