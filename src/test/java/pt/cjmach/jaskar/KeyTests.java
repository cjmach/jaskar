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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import pt.cjmach.jaskar.lib.AskarLibrary;

/**
 *
 * @author cmachado
 */
public class KeyTests {
    
    @Test
    public void givenValidKey_whenSigningMessage_thenVerifiesSignature() {
        try (Key keyPair = Key.generate(KeyAlgorithm.EC_SECP_256R1, false)) {
            byte[] message = "message".getBytes(AskarLibrary.DEFAULT_CHARSET);
            byte[] signature = keyPair.signMessage(message);
            assertTrue(keyPair.verifySignature(message, signature), "Error verifying signature");
            
            byte[] badInput = "bad input".getBytes(AskarLibrary.DEFAULT_CHARSET);
            assertFalse(keyPair.verifySignature(badInput, signature));
            
            byte[] badSignature = "bad signature".getBytes(AskarLibrary.DEFAULT_CHARSET);
            assertFalse(keyPair.verifySignature(message, badSignature));
            
            Throwable cause = assertThrows(AskarException.class, () -> keyPair.verifySignature(message, signature, SignatureAlgorithm.ES384));
            assertNotNull(cause);
        } catch (AskarException ex) {
            fail(ex);
        }
        
    }
}
