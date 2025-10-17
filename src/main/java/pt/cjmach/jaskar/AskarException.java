/*
 *  Copyright 2025 Carlos Machado
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package pt.cjmach.jaskar;

import pt.cjmach.jaskar.lib.ErrorCode;

/**
 *
 * @author cmachado
 */
public final class AskarException extends Exception {

    private final ErrorCode errorCode;

    /**
     *
     */
    AskarException() {
        this(ErrorMessage.create());
    }

    /**
     *
     * @param errorMessage
     */
    private AskarException(ErrorMessage errorMessage) {
        super(errorMessage != null ? errorMessage.message : "Unknown exception message.");
        this.errorCode = errorMessage != null ? ErrorCode.fromCode(errorMessage.code) : ErrorCode.CUSTOM;
    }

    /**
     *
     * @param cause
     */
    AskarException(Throwable cause) {
        super(cause);
        this.errorCode = ErrorCode.CUSTOM;
    }

    /**
     *
     * @return
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
