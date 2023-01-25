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
package org.eclipse.osee.ote.core.log.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ParentLogRecord extends LogRecord {
    private static final long serialVersionUID = 684361479587503820L;
    private Collection<LogRecord> records = new ArrayList<>();

    public ParentLogRecord() {
        super(Level.OFF, "");
    }

    public void addChild(final LogRecord testPoint) {
        records.add(testPoint);
    }

    @JsonProperty
    public Collection<LogRecord> getChildRecords() {
        return records;
    }
    
    @Override
   @JsonIgnore
    public Level getLevel() {
        return super.getLevel();
    };
    
    @Override
   @JsonIgnore
    public String getMessage() {
        return super.getMessage();
    };
}