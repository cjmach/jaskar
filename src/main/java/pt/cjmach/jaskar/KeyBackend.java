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
public enum KeyBackend {

    /**
     *
     */
    SOFTWARE("software"),

    /**
     *
     */
    SECURE_ELEMENT("secure_element");

    private final String backend;
    
    private KeyBackend(String backend) {
        this.backend = backend;
    }

    /**
     * 
     * @return 
     */
    String getBackend() {
        return backend;
    }
    
    /**
     * 
     * @param backend
     * @return 
     */
    static KeyBackend fromBackend(String backend) {
        switch (backend) {
            case "software":
                return KeyBackend.SOFTWARE;
            case "secure_element":
                return KeyBackend.SECURE_ELEMENT;
            default:
                throw new IllegalArgumentException("Unknown backend " + backend);
        }
    }
}
