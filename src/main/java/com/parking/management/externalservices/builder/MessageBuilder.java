package com.parking.management.externalservices.builder;

/**
 * Created by VST on 07-07-2016.
 */
public class MessageBuilder {

    public static String createFeedMessageBody(String name) {
        String message = "{\n" +
                "  \"shortName\": "+ name.replaceAll("\\s+","").toLowerCase() +",\n" +
                "  \"name\": " + name + ",\n" +
                "  \"description\": " + name + ",\n" +
                "  \"isPrivate\": \"false\",\n" +
                "  \"followers\": {\n" +
                "    \"elements\": [\n" +
                "      {\n" +
                "        \"type\": \"Coordinator\",\n" +
                "        \"principal\": {\n" +
                "          \"via\": {\n" +
                "            \"href\": \"/persons/system\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        return message;
    }

    public static String createPostMessageBody(String locationName, String slotName, boolean parked) {

        String content = parked ? "The slot " + slotName + " in the location " + locationName + " is occupied."
                : "The slot " + slotName + " in the location " + locationName + " is free and can be occupied." ;
        String message = "{\n" +
                "  \"content\": \"" + content + "\",\n" +
                "  \"tagAssignments\": {\n" +
                "    \"elements\": [\n" +
                "      {\n" +
                "        \"type\": \"TagAssignment\",\n" +
                "        \"principal\": {\n" +
                "          \"via\": {\n" +
                "            \"href\": \"/tags/" + locationName.replaceAll("\\s+","")+ "\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"TagAssignment\",\n" +
                "        \"principal\": {\n" +
                "          \"via\": {\n" +
                "            \"href\": \"/tags/" + slotName.replaceAll("\\s+","")+ "\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"systemGenerated\": \"true\"\n" +
                "}";
        return message;
    }
}
