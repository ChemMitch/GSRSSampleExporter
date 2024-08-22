package extensions.tests;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gsrs.module.substance.exporters.SubstancePropertyExporter;
import gsrs.module.substance.exporters.SubstancePropertyExporterFactory;
import ix.ginas.exporters.DefaultParameters;
import ix.ginas.exporters.Exporter;
import ix.ginas.exporters.ExporterFactory;
import ix.ginas.exporters.OutputFormat;
import ix.ginas.modelBuilders.SubstanceBuilder;
import ix.ginas.models.v1.Amount;
import ix.ginas.models.v1.Property;
import ix.ginas.models.v1.Substance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.UUID;

public class PropertyExporterTest {

    @Test
    void testGetFormats() {
        SubstancePropertyExporterFactory factory = new SubstancePropertyExporterFactory();
        Set<OutputFormat> formats= factory.getSupportedFormats();
        boolean actual =formats.stream().anyMatch(f->f.getExtension().equalsIgnoreCase("properties.txt")
                && f.getDisplayName().equalsIgnoreCase("Substance Property File(.properties.txt)"));
        Assertions.assertTrue(actual);
    }

    @Test
    void testSupports() {
        ExporterFactory.Parameters parameters = new DefaultParameters( new OutputFormat("properties.txt", "Substance Property File(.properties.txt)"), false);
        SubstancePropertyExporterFactory factory = new SubstancePropertyExporterFactory();
        Assertions.assertTrue(factory.supports(parameters));
    }

    @Test
    void testSupportsNot() {
        ExporterFactory.Parameters parameters = new DefaultParameters( new OutputFormat("txt", "Regular Text File(.txt)"), false);
        SubstancePropertyExporterFactory factory = new SubstancePropertyExporterFactory();
        Assertions.assertFalse(factory.supports(parameters));
    }

    @Test
    void testCreateExporter() throws IOException {
        SubstancePropertyExporterFactory factory = new SubstancePropertyExporterFactory();
        ObjectNode additionalParameters = JsonNodeFactory.instance.objectNode();
        additionalParameters.put("someField", "some value");
        ExporterFactory.Parameters parameters = new DefaultParameters( new OutputFormat("properties.txt",
                "Substance Property File(.properties.txt)"), false, additionalParameters);
        File file = new File("output1.txt");
        FileOutputStream stream = new FileOutputStream(file);

        Exporter<Substance> exporter = factory.createNewExporter(stream, parameters);
        Assertions.assertTrue(exporter instanceof SubstancePropertyExporter);
        file.delete();
    }

    @Test
    void testExport1() throws IOException {
        SubstancePropertyExporterFactory factory = new SubstancePropertyExporterFactory();
        ObjectNode additionalParameters = JsonNodeFactory.instance.objectNode();
        additionalParameters.put("someField", "some value");
        ExporterFactory.Parameters parameters = new DefaultParameters( new OutputFormat("properties.txt",
                "Substance Property File(.properties.txt)"), false, additionalParameters);

        UUID substanceId = UUID.randomUUID();
        String substanceName = "Substance 1";
        String propertyName = "Boiling Point";
        File file = File.createTempFile("report", "properties.txt");
        System.out.printf("report file location: %s\n", file.getAbsolutePath());
        FileOutputStream stream = new FileOutputStream(file);
        SubstanceBuilder builder = new SubstanceBuilder();
        builder.addName(substanceName);

        Property boilingPoint = new Property();
        boilingPoint.setName(propertyName);
        Amount value = new Amount();
        value.low = 100.01;
        value.high=100.12;
        value.units = "C";
        boilingPoint.setValue(value);
        builder.addProperty(boilingPoint);
        builder.setUUID(substanceId);

        Exporter<Substance> exporter = factory.createNewExporter(stream, parameters);
        exporter.export(builder.build());
        exporter.close();
        String fileData= Files.readString(file.toPath());
        Assertions.assertTrue(fileData.contains(substanceName)
                && fileData.contains(substanceId.toString())
                && fileData.contains(propertyName)
                && fileData.contains("100.01"));
        file.delete();
    }

    @Test
    void testExport2() throws IOException {
        SubstancePropertyExporterFactory factory = new SubstancePropertyExporterFactory();
        ObjectNode additionalParameters = JsonNodeFactory.instance.objectNode();
        additionalParameters.put("onlyDefining", true);
        ExporterFactory.Parameters parameters = new DefaultParameters( new OutputFormat("properties.txt",
                "Substance Property File(.properties.txt)"), false, additionalParameters);

        UUID substanceId = UUID.randomUUID();
        String substanceName = "Substance 1";
        String propertyName = "Boiling Point";
        File file = File.createTempFile("report", "properties.txt");
        System.out.printf("report file location: %s\n", file.getAbsolutePath());
        FileOutputStream stream = new FileOutputStream(file);
        SubstanceBuilder builder = new SubstanceBuilder();
        builder.addName(substanceName);

        Property boilingPoint = new Property();
        boilingPoint.setName(propertyName);
        Amount value = new Amount();
        value.low = 100.01;
        value.high=100.12;
        value.units = "C";
        boilingPoint.setValue(value);
        boilingPoint.setDefining(false);
        builder.addProperty(boilingPoint);
        builder.setUUID(substanceId);

        Exporter<Substance> exporter = factory.createNewExporter(stream, parameters);
        exporter.export(builder.build());
        exporter.close();
        String fileData= Files.readString(file.toPath());
        Assertions.assertTrue(fileData.contains(substanceName)
                && fileData.contains(substanceId.toString())
                && !fileData.contains(propertyName)
                && !fileData.contains("100.01"));
        file.delete();
    }

}
