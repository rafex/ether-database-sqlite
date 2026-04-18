package dev.rafex.ether.database.sqlite.errors;

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

import java.sql.SQLException;
import java.util.Objects;

public final class SQLiteErrorClassifier {

    public enum Category {
        UNIQUE_VIOLATION, FOREIGN_KEY_VIOLATION, NOT_NULL_VIOLATION, CHECK_VIOLATION, CONCURRENCY_CONFLICT, OTHER
    }

    private SQLiteErrorClassifier() {
    }

    public static Category classify(final SQLException exception) {
        Objects.requireNonNull(exception, "exception");
        final int errorCode = exception.getErrorCode();
        if (errorCode == SQLiteErrorCodes.UNIQUE_VIOLATION) {
            return Category.UNIQUE_VIOLATION;
        }
        if (errorCode == SQLiteErrorCodes.FOREIGN_KEY_VIOLATION) {
            return Category.FOREIGN_KEY_VIOLATION;
        }
        if (errorCode == SQLiteErrorCodes.NOT_NULL_VIOLATION) {
            return Category.NOT_NULL_VIOLATION;
        }
        if (errorCode == SQLiteErrorCodes.CHECK_VIOLATION) {
            return Category.CHECK_VIOLATION;
        }
        if (errorCode == SQLiteErrorCodes.BUSY || errorCode == SQLiteErrorCodes.LOCKED) {
            return Category.CONCURRENCY_CONFLICT;
        }
        return Category.OTHER;
    }
}
