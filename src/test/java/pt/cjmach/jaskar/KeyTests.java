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

import java.security.SecureRandom;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import pt.cjmach.jaskar.lib.AskarLibrary;

/**
 *
 * @author cmachado
 */
public class KeyTests {

    @Test
    public void givenSupportedBackends_whenInspecting_thenIncludesSoftware() {
        try {
            KeyBackend[] backends = Key.getSupportedBackends();
            assertEquals(1, backends.length);
            assertEquals(KeyBackend.SOFTWARE, backends[0]);
        } catch (AskarException ex) {
            fail(ex);
        }
    }

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
    
    @Test
    public void givenValidKey_whenWrappingKey_thenUnwrappedKeyIsEqual() {
        try (Key key = Key.generate(KeyAlgorithm.AES_A256_GCM, true); Key other = Key.generate(KeyAlgorithm.ED25519, true)) {
            byte[] nonce = new byte[12];
            new SecureRandom().nextBytes(nonce);
            WrappedSecret wrapped = key.wrapKey(other, nonce);
            
            Key unwrapped = key.unwrapKey(KeyAlgorithm.ED25519, wrapped);
            assertEquals(other.getJwkThumbprint(), unwrapped.getJwkThumbprint());
        } catch (AskarException ex) {
            fail(ex);
        }
    }
}
