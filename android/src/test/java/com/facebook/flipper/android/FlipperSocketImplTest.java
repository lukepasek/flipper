
package com.facebook.flipper.android;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.security.KeyStore;
import java.security.KeyStoreException;

@RunWith(RobolectricTestRunner.class)
public class FlipperSocketImplTest {

    @Test
    public void shoudInitialiseFlipperSocketImplWithStaticConfig() {
        StaticFlipperClientConfig.enabled = true;
        StaticFlipperClientConfig.caCertifcate = "MIIDgzCCAmugAwIBAgIUI9/Rytx7VaguvGEf3P/rI6VTmRwwDQYJKoZIhvcNAQELBQAwUTELMAkGA1UEBhMCVVMxCzAJBgNVBAgMAkNBMRMwEQYDVQQHDApNZW5sbyBQYXJrMQ4wDAYDVQQKDAVTb25hcjEQMA4GA1UEAwwHU29uYXJDQTAeFw0yMzA1MjIwOTI5NDVaFw0yMzA2MjEwOTI5NDVaMFExCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJDQTETMBEGA1UEBwwKTWVubG8gUGFyazEOMAwGA1UECgwFU29uYXIxEDAOBgNVBAMMB1NvbmFyQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC4Ise7oqr/NZKYyKTebquDjdhnT21To599HWBaN0drY1oYoClh0JRh/W7tTh6RuPlvQ3kqjeNtumUakZnsIsZWl6LjP8pWXGZoOEhlajEVkAE6gwfqBIeHnK7oXfFoQB5iD8k0uSDIgKQvUbTpB/E80FnfxfaGmZxyR9jBuAOO16hjPZLgggAvnN4pVYS3vgxVYfbUmjcEKd78ekWMSzkkDOGWKKgCINkEIAjR2iuejR9KR9RpZytIW7hAG54E1Mlk6FQJ71WDiNdLYMBcXcbCgIZH3GSxsbvjK6rRTw454Zrce2ijNsX9JQnBtKQD4zdMU+l9qwAv+gOtJTYiNPBNAgMBAAGjUzBRMB0GA1UdDgQWBBRw5faxEKjcawcWDcBNa4J3UobboDAfBgNVHSMEGDAWgBRw5faxEKjcawcWDcBNa4J3UobboDAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQAXlhDTa5qhSeBqj4kv27EoqQj667F8flSpqZOhUt9JKN0ro4w7+8vxkLFQonPpcnKIte6yctKmSVBUffhBF+hfiIxBaj1o64z4pzcKeBma36/80myyos1HdVETI8qDp2PQiNWd7TsMzrHoWyP34Mm45C4TRt9IVD3Ani0qKrYYNC2gkVEtX6c2RseNiRBdPbrmX4hQO+V2DA9+ZCEi+q/4Jo+FROY+HD9zIxc/oG0o7BTJkEa5fNTluH1CwiVNY7BTV2gQI3lvsxo5bk2kOE60E06cWqiv5BouW//X4gvaIXBl5rNJ4ye9hdrjv3Gx2FsY8zs+ex5yVGQOl0RS78UM";
        StaticFlipperClientConfig.clientCertificate = "MIIDTTCCAjUCFBooHDpqmwehcdFLg7W9ktuR90FiMA0GCSqGSIb3DQEBCwUAMFExCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJDQTETMBEGA1UEBwwKTWVubG8gUGFyazEOMAwGA1UECgwFU29uYXIxEDAOBgNVBAMMB1NvbmFyQ0EwHhcNMjMwNTI0MTQzMTUyWhcNMjMwNjIzMTQzMTUyWjB1MQswCQYDVQQGEwJVUzELMAkGA1UECAwCQ0ExEzARBgNVBAcMCk1lbmxvIFBhcmsxEDAOBgNVBAoMB0ZsaXBwZXIxMjAwBgNVBAMMKWNvbS5zY2hhbmdlLmNsaWVudC52YW5pbGxhLmFuZHJvaWQubW9iaWxlMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5pkmn8IUyvrNCM9nXYlpT1gMq8qyslU5+0ncWIAeKADFO/m9SkHrvlrx+zcky9U2FreP4idcqPaEs3GW7hJWY0Asm5EgwgnCZMkHekuW+3o9gP+qrXIQHhN4ctw/ARItVD5I/DIT4l5m+NzYTYFnad4qszMN7+XTdiHHg7f/YEEt0ZrSawfW/wB1jRpkdAvHsldMsRUY3+2DhSEF7wbOJESousk2CM7Auwb4kTV8lqHk8nXTzK+aNg7UBYaLrLBdRrAViDRycVYH1EQwe3gU7VE8H9ePRa2l4E0Y7eB1SrJqiXr0DTOQ14neABhVeRMxFnyVEeBbD+ZMYeonKwxkgwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQBRKsghIn3JgddPr8i4t4cnJW+T2K7CISEWFLBlxtoVGf3DwtHGKh4OoccMcwKloRV7lodhVMxqTOONco5BzyGbGwLA4rvkOS4NCXQVyIKuJce6v3rr8f6csiQjP/RMH+gnsAVKHoFs53nzO64RUShDrE26JW3XGUriIsxORw1ybTSRLpoHB8U/IN1j3r/bwGqoj7qX2PrbqhggZUgRnMLrosRqlaohyO+S8yut6J8bWLThRu8KssPpYzuji3J07Sq/J2W73BX7Zzv+gN5WOypI4Hi+Luwgq3Ky+KX45wJMOPGxRyF/KOgRVYxzalwN5v/vjomvFvDZGV1+itkWMmEK";
        StaticFlipperClientConfig.clientKey = "MIIEpAIBAAKCAQEA5pkmn8IUyvrNCM9nXYlpT1gMq8qyslU5+0ncWIAeKADFO/m9SkHrvlrx+zcky9U2FreP4idcqPaEs3GW7hJWY0Asm5EgwgnCZMkHekuW+3o9gP+qrXIQHhN4ctw/ARItVD5I/DIT4l5m+NzYTYFnad4qszMN7+XTdiHHg7f/YEEt0ZrSawfW/wB1jRpkdAvHsldMsRUY3+2DhSEF7wbOJESousk2CM7Auwb4kTV8lqHk8nXTzK+aNg7UBYaLrLBdRrAViDRycVYH1EQwe3gU7VE8H9ePRa2l4E0Y7eB1SrJqiXr0DTOQ14neABhVeRMxFnyVEeBbD+ZMYeonKwxkgwIDAQABAoIBAGG/IYQuvKmdzjOh1urrSFx9skFP1peJUN6X3HMXO7ExQmm93uMPNmKmIlSed1Z/tkHC5ZazoK9ub6mwyI158Gx5027OAdi+UkxuemD4kbNfiZqYxTxoUTSuhCwL+Bj4H4bKMB3XDF60LWnoEzgnVjKmHeuABLRf81br0qqe3/1oVXm+vWiR2SjWnrxOqTA20RYrFo4L4jcdouuBlL8GJnGvEYTehRoAoUt2WiyRdYsBLv6QKyWfrSS2nBot1/tUq0Osa3/pvk2VixgkV+NkOu2ZKcdK1mJi8oxXYVbZzkJjD6KVvOGzMpxmPomJu7fyLn26eXE7eQIPo7XiN+8p4uECgYEA+XtKxSbA4/jAnTztU1Af4zL1qUVqm4gDX3qwTioBez8RDJg2aAufLXz707CINYzAnhl//SZHuzkwRKld1e180ycVkPYtoQVdbFgLiWjfYMulXTbgxcMAoMR5+EY9OUSyfybvIgC8BYBFP6m7Gn3AYHpkUAkzjyLFpsZw8yUrLPcCgYEA7J+NumjQNDT0UhM7WvJOcnC+9sDBc58OzHw5yuUZnueCiWkDuo70kS1itfkCL5c3jM/mc5/t1eEm8pUqRmpqN6mA8aOif6uOz1VNHDgxlPeTQNh7QBTH4g91X9nZqwKiFucV+wsaNZGl55UP9UMTD0Y8jG+65MsZS5QezZw4HdUCgYEAtPfVReuVkgiYF7bhZETDzLfjcTUUXturrq+9ggWAa5lU0bD8Dj8X6RQ3S9hLBaUi98wBhm0lLU8Pj07X7V1G5Zf9UdjxQlM08GlxfOxg1MMEIfiz1WBdbUD4RLsWoBsP66IfZqfnu4nPMZ36Rf4f9Rvy9gemqdaMTyy1M/qrNgMCgYAmXnz55DMWtBuGD45JYg8saXHdy0XTPNCGggJRDzNHXB0DsR6kqrenrudZwKYFF40vbSreRumnC9pn7Z4sSucsXXUGOdWIxEXlAtz9HXOUx0x+cFwT+QqscntC7q1oGIY2FoSu747xbsurMLWkvJqjmG0LDLcOkIxqf/s6tgE3fQKBgQDJe3Y3hPIs777LlVNy9mNAfPJisChhifY//abN/z/+9aWQBkfMSE7k52L9GGUcWpgyVfCL3Wcr29KvFkZj2gCwHFfRRbSAsU2AeSOVJY4YJt2rerlIWh5B2frAXOFjaaWWtx9vLHur/QLUmgO+mbPkmvjxbJf6BMI23mc8Lln2hg";
        StaticFlipperClientConfig.clientKeyPassword = "fl1pp3r";
        StaticFlipperClientConfig.clientAlias = "key_alias_1";

        assertEquals(StaticFlipperClientConfig.isEnabled(), true);
        boolean gotAuthObject = StaticFlipperClientConfig.getAuthenticationObject() != null;
        boolean gotCaCertificate = StaticFlipperClientConfig.getCaCertificate() != null;
        Exception keystoreException = null;
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            StaticFlipperClientConfig.loadClientKeyStore(ks);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            keystoreException = e;
        }
        assertEquals(gotAuthObject, true);
        assertEquals(gotCaCertificate, true);
        assertEquals(keystoreException, null);
    }
}
