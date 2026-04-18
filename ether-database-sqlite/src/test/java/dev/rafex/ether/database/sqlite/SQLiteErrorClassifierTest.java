package dev.rafex.ether.database.sqlite;

/*-
 * #%L
 * ether-database-sqlite
 * %%
 * Copyright (C) 2025 - 2026 Raúl Eduardo González Argote
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import dev.rafex.ether.database.sqlite.errors.SQLiteErrorClassifier;
import dev.rafex.ether.database.sqlite.errors.SQLiteErrorCodes;

class SQLiteErrorClassifierTest {

    @Test
    void shouldClassifyKnownErrorCodes() {
        // SQLException constructor: (String reason, String sqlState, int vendorCode)
        assertEquals(SQLiteErrorClassifier.Category.UNIQUE_VIOLATION,
                SQLiteErrorClassifier.classify(new SQLException("duplicate", null, SQLiteErrorCodes.UNIQUE_VIOLATION)));
        assertEquals(SQLiteErrorClassifier.Category.CONCURRENCY_CONFLICT,
                SQLiteErrorClassifier.classify(new SQLException("locked", null, SQLiteErrorCodes.LOCKED)));
        assertEquals(SQLiteErrorClassifier.Category.FOREIGN_KEY_VIOLATION,
                SQLiteErrorClassifier.classify(new SQLException("foreign key", null, SQLiteErrorCodes.FOREIGN_KEY_VIOLATION)));
        assertEquals(SQLiteErrorClassifier.Category.NOT_NULL_VIOLATION,
                SQLiteErrorClassifier.classify(new SQLException("not null", null, SQLiteErrorCodes.NOT_NULL_VIOLATION)));
        assertEquals(SQLiteErrorClassifier.Category.CHECK_VIOLATION,
                SQLiteErrorClassifier.classify(new SQLException("check", null, SQLiteErrorCodes.CHECK_VIOLATION)));
        assertEquals(SQLiteErrorClassifier.Category.CONCURRENCY_CONFLICT,
                SQLiteErrorClassifier.classify(new SQLException("busy", null, SQLiteErrorCodes.BUSY)));
    }

    @Test
    void shouldReturnOtherForUnknownErrorCode() {
        assertEquals(SQLiteErrorClassifier.Category.OTHER,
                SQLiteErrorClassifier.classify(new SQLException("unknown", null, 9999)));
    }
}
