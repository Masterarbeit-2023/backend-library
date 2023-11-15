package io.github.masterarbeit.generator;

import io.github.masterarbeit.generator.config.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.masterarbeit.generator.YamlReader.readYaml;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class YamlReaderTest {

    @Test
    void readYamlTest() {
        Map<String, Function> map = new HashMap<>();
        Function function1 = new Function(ProviderEnum.AWS, "none");
        Function function2 = new Function(ProviderEnum.AZURE, "none");
        map.put("function_1", function1);
        map.put("function_2", function2);

        Configuration expectedConfiguration = new Configuration(
                new Provider("azureKey"),
                new Provider("awsKey"),
                new Provider("googleKey"),
                new OnPremise("ip-address"),
                ProviderEnum.AZURE,
                ProviderEnum.AZURE,
                "yes",
                "org.example",
                ProviderEnum.AZURE,
                Infrastructure.TRADITIONAL,
                "none",
                map
        );
        Configuration actualConfiguration = readYaml("config.yaml");

        assertNotEquals(null, actualConfiguration);
        assertEquals(actualConfiguration, expectedConfiguration);
    }
}