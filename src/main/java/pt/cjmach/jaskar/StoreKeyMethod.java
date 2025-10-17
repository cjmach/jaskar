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

/**
 *
 * @author cmachado
 */
public enum StoreKeyMethod {

    /**
     *
     */
    RAW("raw"),

    /**
     *
     */
    NONE("none"),

    /**
     *
     */
    ARGON2I_MOD("kdf:argon2i:mod"),

    /**
     *
     */
    ARGON2I_INT("kdf:argon2i:int");
    
    private final String method;

    private StoreKeyMethod(String method) {
        this.method = method;
    }

    String getMethod() {
        return method;
    }
}
