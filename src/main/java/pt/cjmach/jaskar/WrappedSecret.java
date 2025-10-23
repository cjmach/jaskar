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

import java.util.Arrays;
import pt.cjmach.jaskar.lib.EncryptedBuffer;

/**
 * The result of an AEAD encryption operation.
 * 
 * @author cmachado
 */
public class WrappedSecret {
    private final byte[] data;
    private final int tagPosition;
    private final int noncePosition;

    WrappedSecret(EncryptedBuffer encryptedBuffer) {
        this.data = encryptedBuffer.buffer.getBytes();
        this.tagPosition = (int) encryptedBuffer.tag_pos;
        this.noncePosition = (int) encryptedBuffer.nonce_pos;
    }
    
    /**
     * Gets the ciphertext.
     * 
     * @return 
     */
    public byte[] getCiphertext() {
        byte[] ciphertext = Arrays.copyOfRange(data, 0, tagPosition);
        return ciphertext;
    }
    
    /**
     * Gets the nonce.
     * 
     * @return 
     */
    public byte[] getNonce() {
        byte[] nonce = Arrays.copyOfRange(data, noncePosition, data.length);
        return nonce;
    }
    
    /**
     * Gets the authentication tag.
     * 
     * @return 
     */
    public byte[] getTag() {
        byte[] tag = Arrays.copyOfRange(data, tagPosition, noncePosition);
        return tag;
    }
}
