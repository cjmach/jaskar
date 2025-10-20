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
 * An algorithm used for signing and verification.
 * 
 * @author cmachado
 */
public enum SignatureAlgorithm {

    /**
     *
     */
    EDDSA("eddsa"),

    /**
     * Gets an ECDSA with a P-256 curve SignatureAlgorithm as described in https://tools.ietf.org/html/rfc7518.
     */
    ES256("es256"),

    /**
     * Gets an ECDSA with a secp256k1 curve SignatureAlgorithm as described in https://tools.ietf.org/html/rfc7518.
     */
    ES256K("es256k"),

    /**
     * Gets an ECDSA with a P-384 curve SignatureAlgorithm as described in https://tools.ietf.org/html/rfc7518.
     */
    ES384("es384");
    
    private final String algorithm;

    private SignatureAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 
     * @return 
     */
    String getAlgorithm() {
        return algorithm;
    }
}
