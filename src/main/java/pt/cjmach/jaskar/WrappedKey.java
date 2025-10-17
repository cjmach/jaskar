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

import pt.cjmach.jaskar.lib.EncryptedBuffer;

/**
 *
 * @author cmachado
 */
public class WrappedKey {
    private final byte[] data;
    private final int tagPosition;
    private final int noncePosition;

    WrappedKey(EncryptedBuffer encryptedBuffer) {
        this.data = new byte[(int) encryptedBuffer.buffer.len];
        this.tagPosition = (int) encryptedBuffer.tag_pos;
        this.noncePosition = (int) encryptedBuffer.nonce_pos;
    }
    
    

    /**
     * @return the data
     */
    public byte[] getData() {
        return (byte[]) data.clone();
    }

    /**
     * @return the tagPosition
     */
    public int getTagPosition() {
        return tagPosition;
    }

    /**
     * @return the noncePosition
     */
    public int getNoncePosition() {
        return noncePosition;
    }
}
