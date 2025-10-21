/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pt.cjmach.jaskar;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import pt.cjmach.jaskar.lib.AskarLibrary;

/**
 *
 * @author mach
 */
public class KeyTests {
    
    @Test
    public void givenValidKey_whenSigningMessage_thenVerifiesSignature() {
        try (Key keyPair = Key.generate(KeyAlgorithm.EC_SECP_256R1, false)) {
            byte[] message = "message".getBytes(AskarLibrary.DEFAULT_CHARSET);
            byte[] signature = keyPair.signMessage(message);
            assertTrue(keyPair.verifySignature(message, signature), "Error verifying signature");
            System.out.println("[INFO] Verified message signature.");
            byte[] badInput = "bad input".getBytes(AskarLibrary.DEFAULT_CHARSET);
            assertFalse(keyPair.verifySignature(badInput, signature));
            System.out.println("[INFO] Not verified bad input.");
            
            byte[] badSignature = "bad signature".getBytes(AskarLibrary.DEFAULT_CHARSET);
            assertFalse(keyPair.verifySignature(message, badSignature));
            System.out.println("[INFO] Not verified bad signature.");
            
            Throwable cause = assertThrows(AskarException.class, () -> keyPair.verifySignature(message, signature, SignatureAlgorithm.ES384));
            assertNotNull(cause);
            System.out.println("[INFO] Verified exception thrown.");
        } catch (AskarException ex) {
            fail(ex);
        }
        
    }
}
