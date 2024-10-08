package gsrs.extensions.exporters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ix.ginas.exporters.Exporter;
import ix.ginas.exporters.ExporterFactory;
import ix.ginas.exporters.OutputFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

public class SubstancePropertyExporterFactory implements ExporterFactory {

    public static final String DEFINING_PARAMETER_NAME ="onlyDefining";

    private static final Set<OutputFormat> formats = Collections.singleton( new OutputFormat("properties.txt", "Substance Property File(.properties.txt)"));

    @Override
    public boolean supports(Parameters params) {
        return "properties.txt".equals(params.getFormat().getExtension());
    }

    @Override
    public Set<OutputFormat> getSupportedFormats() {
        return formats;
    }

    @Override
    public Exporter createNewExporter(OutputStream out, Parameters params) throws IOException {
        SubstancePropertyExporter exporter = new SubstancePropertyExporter(out, params);
        return exporter;
    }

    @Override
    public JsonNode getSchema() {
        ObjectNode parameters = JsonNodeFactory.instance.objectNode();
        ObjectNode parameterNode = JsonNodeFactory.instance.objectNode();
        parameterNode.put("type", "boolean");
        parameterNode.put("title", "Restrict output to defining properties?");
        parameterNode.put("comments", "Limit output to properties marked as defining?");
        parameters.set(DEFINING_PARAMETER_NAME, parameterNode);
        return generateSchemaNode("Substance Property Exporter Parameters", parameters);
    }

}
