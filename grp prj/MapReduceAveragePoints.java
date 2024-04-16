import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class MapReduce {
    public static void map(String line, Map<Integer, List<Double>> output) {
        // Split the line by dot to separate the id and the points
        String[] tokens = line.split("\\.");
        if (tokens.length == 2) {
            // Extract the id and points from tokens
            int subjectId = Integer.parseInt(tokens[0].substring(0, 1)); // Take the first digit as the subject id
            double points;
            if (tokens[0].substring(1).equals("10")) {
                points = Double.parseDouble(tokens[0].substring(1));
            } else {
                points = Double.parseDouble(tokens[0].substring(1, 2) + "." + tokens[1]);
            }

            // Check if the subject ID already exists in the output map
            if (!output.containsKey(subjectId)) {
                // If it doesn't exist, create a new list and put it into the map
                output.put(subjectId, new ArrayList<>());
            }

            // Add the points to the list associated with the subject ID
            output.get(subjectId).add(points);
        }
    }

    public static void reduce(Map<Integer, List<Double>> input) {
        Map<Integer, Double> sum = new HashMap<>();
        Map<Integer, Integer> count = new HashMap<>();
        // Aggregate points and count for each subject
        for (Map.Entry<Integer, List<Double>> entry : input.entrySet()) {
            int subjectId = entry.getKey();
            List<Double> pointsList = entry.getValue();

            double totalPoints = 0;
            for (double points : pointsList) {
                totalPoints += points;
            }
            sum.put(subjectId, totalPoints);
            count.put(subjectId, pointsList.size());
        }

        for (Map.Entry<Integer, Double> entry : sum.entrySet()) {
            int subjectId = entry.getKey();
            double totalPoints = entry.getValue();
            int totalCount = count.get(subjectId);
            double average = totalPoints / totalCount;
            System.out.println("Subject ID: " + subjectId + ", Average Points: " + average);
        }
    }

    public static void main(String[] args) throws IOException {
        Map<Integer, List<Double>> input = new HashMap<>();


        try (BufferedReader br = new BufferedReader(new FileReader("input.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                map(line, input);
            }
        }
        reduce(input);
    }
}
