package stream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonitoredData {

	private String start_time;
	private String end_time;
	private String activity;
	private static ArrayList<MonitoredData> md = new ArrayList<MonitoredData>();

	public MonitoredData(String start_time, String end_time, String activity) {
		super();
		this.start_time = start_time;
		this.end_time = end_time;
		this.activity = activity;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	@Override
	public String toString() {
		return "MonitoredData [start_time=" + start_time + ", end_time=" + end_time + ", activity=" + activity + "]";
	}

	public static void totalOfDays() {
		System.out.println("\n2.Days of monitored data in the log: "
				+ md.stream().map(m -> m.getStart_time().substring(8, 10)).distinct().count());

	}

	public static void totalOfDays2() {
		System.out.println("\n2.Days of monitored data in the log: ");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long diff = dateFormat
					.parse(md.stream().map(s -> s.getEnd_time()).reduce((first, second) -> second).orElse(null))
					.getTime()
					- dateFormat.parse(
							(md.stream().map(s -> s.getStart_time()).reduce((first, second) -> first).orElse(null)))
							.getTime();
			System.out.println((int) diff / (24 * 60 * 60 * 1000) + 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void countActivitiesAll() {
		System.out.println("\n3.How many times has appeared each activity over the entire monitoring period");

		Map<String, Long> counter2 = md.stream().map(m -> m.getActivity())
				.collect(Collectors.groupingBy(s -> s, Collectors.counting()));

		Stream.of(counter2.toString()).forEach(System.out::println);
	}

	public static String diffDates(MonitoredData m) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int diffhours = 0, diffmin = 0, diffsec = 0;
		try {
			long diff = dateFormat.parse(m.end_time).getTime() - dateFormat.parse(m.start_time).getTime();
			diffhours = (int) (diff / (60 * 60 * 1000));
			diffmin = (int) (diff / (60 * 1000)) - (diffhours * 60);
			diffsec = (int) (diff / (1000)) - (diffhours * 3600) - (diffmin * 60);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return diffhours + ":" + diffmin + ":" + diffsec;
	}

	public static void dateSum() {

		Map<String, String> hashjoin = new HashMap<String, String>();
		hashjoin = (Map<String, String>) md.stream().collect(
				Collectors.toMap(s -> s.getActivity(), s -> MonitoredData.diffDates(s), (s1, s2) -> s1 + " " + s2));
		System.out.println("\n6.The entire duration over the monitoring period for each activity:");
		String k = "";
		ArrayList<String> ret = new ArrayList<String>();
		for (String name : hashjoin.keySet()) {
			String key = name.toString();
			String value = hashjoin.get(name).toString();
			String[] splited = value.split(" ");
			int count = 0;
			for (String ss : splited) {
				String[] splited2 = ss.split(":");
				int var0 = 3600 * Integer.parseInt(splited2[0]);
				int var1 = 60 * Integer.parseInt(splited2[1]);
				int var2 = Integer.parseInt(splited2[2]);
				count = count + var0 + var1 + var2;
			}
			int h = 0, m = 0, s = 0;
			h = (int) count / 3600;
			m = (int) count / 60 - h * 60;
			s = count - 3600 * h - 60 * m;
			k = key + "  " + String.valueOf(h) + ":" + String.valueOf(m) + ":" + String.valueOf(s);
			ret.add(k);
		}
		ret.stream().forEach(System.out::println);

	}

	public static void durationOnEachLine() {
		System.out.println("\n5.Duration of the activity on each line");
		md.stream().map(s -> diffDates(s)).collect(Collectors.toList()).forEach(System.out::println);
	}

	public static void countForEachDay() {
		System.out.println("\n4.How many times has appeared each activity for each day over the monitoring period");
		System.out.println(md.stream().collect(Collectors.groupingBy(
				s -> "day:" + s.getStart_time().substring(8, 10) + "->" + s.getActivity(), Collectors.counting())));
	}

	public static void main(String[] args) {

		String fileName = "D:\\PT2019\\pt2019_30226_nimigean_emanuelaionela_assignment_5\\Activities.txt";
		ArrayList<String> list = new ArrayList<String>();

		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			list = (ArrayList<String>) stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String s : list) {
			String[] splited = s.split("	");
			md.add(new MonitoredData(splited[0], splited[2], splited[4]));
		}

		md.stream().forEach(System.out::println);

/////////////////////////// 2.Count how many days of monitored data appears in the log. 
	//	totalOfDays();
///////////////////////////// 3.Count how many times has appeared each activity over the entire monitoring period. 
		countActivitiesAll();
////////////////////////////// 5. duration of the activity on current line:
		durationOnEachLine();
///////////////////////////// 6. The entire duration over the monitoring period for each activity:	
		dateSum();
//////////////////////////// 4. How many times has appeared each activity for each day over the monitoring period
		countForEachDay();
/////////////////////////// 2.2.Count how many days of monitored data appears in the log. 
		totalOfDays2();

	}
}
