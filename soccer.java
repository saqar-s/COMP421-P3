import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
public class soccer {
    public static void main(String args[]) throws SQLException, ParseException {

        // Load the DB2 JDBC Driver - Alternative 2
        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (Exception e) {
            System.out.println("Driver Registration Failed");
        }

        // Connect to the database
        String url = "jdbc:db2://winter2023-comp421.cs.mcgill.ca:50000/cs421";
        String user = "cs421g46";
        String password = "";

        if (url == null || user == null || password == null){
            System.err.println("ERROR: url, user and password required");
            System.exit(1);
        }

        Connection conn = DriverManager.getConnection(url, user, password);

        if (conn != null) {
            System.out.println("Connection Successful: " + conn);
        }

        // Create a statement object to submit SQL statements to the driver
        Statement statement = conn.createStatement();

        // Print the menu and use a scanner to get the variables

        Scanner sc = new Scanner(System.in);
        int menu_code = 0;

        menu: while(true){
	        System.out.println("");
            System.out.println("Soccer Main Menu");
            System.out.println("\t 1. List information of matches of a country");
            System.out.println("\t 2. Insert initial player information for a match");
            System.out.println("\t 3. Purchase a match ticket!");
            System.out.println("\t 4. Exit application");
            System.out.println("");
            System.out.print("Please Enter Your Option: ");

            try {
                String user_input = sc.nextLine();
                menu_code = Integer.parseInt(user_input);
            } catch (Exception e){
                System.out.println("Input Error: Please enter a number only");
                continue menu;
            }

            // MENU ITEM 1 /////////////////////////////////////////////////////////////////////////////////////////////

            if (menu_code == 1){

                menu1: while (true) {

                    System.out.println("");
                    System.out.print("Please enter name of a participating country: ");
                    String country = sc.nextLine();

                    String country_info = "SELECT pt1.matchid, pt1.country AS home_team, pt2.country AS away_team , match.time as date, match.stage, match.home_goals, match.away_goals, seatstbl.seats\n" +
                            "FROM playingteam AS pt1 JOIN playingteam AS pt2 ON pt1.matchid = pt2.matchid, (SELECT COUNT(ticketid) AS seats, match.matchid\n" +
                            "FROM matchticket\n" +
                            "FULL OUTER JOIN match ON match.matchid = matchticket.matchid\n" +
                            "GROUP By match.matchid, match.time\n" +
                            "ORDER By Match.time) AS seatstbl, Match\n" +
                            "WHERE pt1.status = 'Home' AND pt2.status = 'Away'AND match.matchid = pt1.matchid AND match.matchid = seatstbl.matchid AND (pt1.country = '" + country + "' OR pt2.country = '" + country + "')";

                    System.out.println("");
                    System.out.printf("| %-20s | %-20s | %20s | %20s | %20s | %20s | %20s |%n", "Home Team", "Away Team", "Date and Time", "Stage", "Home Team Goals", "Away Team Goals", "Seats Sold");

                    try {
                        ResultSet rs = statement.executeQuery(country_info);

                        while(rs.next()){
                            String home_team = rs.getString("home_team");
                            String away_team = rs.getString("away_team");
                            String stage = rs.getString("stage");
                            String date = rs.getTimestamp("date").toString();
                            int home_goals = rs.getInt("home_goals");
                            int away_goals = rs.getInt("away_goals");
                            int seats = rs.getInt("seats");

                            System.out.println("");
                            System.out.printf("| %-20s | %-20s | %20s | %20s | %20s | %20s | %20s |%n", home_team, away_team, date, stage, home_goals, away_goals, seats);
                        }
                    } catch (SQLException e){
                        System.out.println(e);
                    }

                    System.out.println("");
                    System.out.print("Enter [A] to find matches of another country, [P] to go to the previous menu: ");

                    String u_input = sc.nextLine();

                    if (u_input.equals("A")){
                        continue menu1;
                    }

                    else {
                        break;
                    }
                }

                continue menu;
            }

            // MENU ITEM 2 /////////////////////////////////////////////////////////////////////////////////////////////

            else if (menu_code == 2) {

                    System.out.println("");
                    System.out.println("Matches in the coming 3 days:");
                    System.out.println("");

                    System.out.printf("| %-20s | %-20s | %20s | %20s | %20s |%n", "Match ID", "Home Team", "Away Team", "Date and Time", "Stage");

                    String list_of_matches = "SELECT pt1.matchid, pt1.country AS home_team, pt2.country AS away_team , match.time as date, match.stage\n" +
                            "FROM playingteam AS pt1 JOIN playingteam AS pt2 ON pt1.matchid = pt2.matchid, match\n" +
                            "WHERE pt1.status = 'Home' AND pt2.status = 'Away'AND match.matchid = pt1.matchid  AND match.time >= CURRENT_TIMESTAMP AND match.time <= ADD_DAYS(CURRENT_TIMESTAMP,3)\n" +
                            "ORDER BY pt1.matchid";

                    // TODO: Get the list of matches in the coming 3 days from current time
                    try {
                        ResultSet rs = statement.executeQuery(list_of_matches);

                        while(rs.next()){
                            int match_id = rs.getInt("matchid");
                            String home_team = rs.getString("home_team");
                            String away_team = rs.getString("away_team");
                            String date = rs.getTimestamp("date").toString();
                            String stage = rs.getString("stage");

                            System.out.println("");
                            System.out.printf("| %-20s | %-20s | %20s | %20s | %20s |%n", match_id, home_team, away_team, date, stage);
                        }
                    } catch (SQLException e){
                        System.out.println(e);
                    }

                    System.out.println("");

                    System.out.println("NOTE: To go to previous menu, press [P]");
                    System.out.print("Please enter the match ID for which you wish to add players: ");
                    String query2_match_id = sc.nextLine();

                    if (query2_match_id.equals("P")){
                        continue;
                    }

                    System.out.println("");

                    System.out.println("NOTE: To go to previous menu, press [P]");
                    System.out.print("Please enter the country for which you wish to add players: ");
                    String query2_country = sc.nextLine();

                    if (query2_country.equals("P")){
                        continue;
                    }

                    // TODO: Print all the current registered players to play for the entered country
                    System.out.println("");
                    System.out.println("The following players from " + query2_country + " are already entered for match " + query2_match_id + ":");
                    System.out.println("");
                    int players_added = 0;

                    String registered_players = "SELECT player.name, player.shirtnumber, playerperformance.detailedposition,playerperformance.entertime, playerperformance.leavingtime, playerperformance.yellowcard, playerperformance.redcard \n" +
                            "FROM player, playerperformance, teammember\n" +
                            "WHERE player.emailaddress = playerperformance.emailaddress and player.emailaddress = teammember.emailaddress AND playerperformance.startingstatus = 'starting XI' AND teammember.country = '" + query2_country + "' AND playerperformance.matchid = '" + query2_match_id + "'";

                    System.out.println("");
                    System.out.printf("| %-20s | %-20s | %20s | %20s | %20s | %20s | %20s |%n", "Player Name", "Shirt Number", "Detailed Position", "From Minute", "To Minute", "Yellow Cards", "Red Cards");

                    try {
                        ResultSet rs = statement.executeQuery(registered_players);

                        while(rs.next()){

                            String player_name = rs.getString("name");
                            int shirt_number = rs.getInt("shirtnumber");
                            String detailed_position = rs.getString("detailedposition");
                            int enter_time = rs.getInt("entertime");
                            int leaving_time = rs.getInt("leavingtime");
                            int yellow_card = rs.getInt("yellowcard");
                            int red_card = rs.getInt("redcard");
                            players_added = players_added + 1;

                            System.out.println("");

                            if (leaving_time == 0){
                                System.out.printf("| %-20s | %-20s | %20s | %20s | %20s | %20s | %20s |%n", player_name, shirt_number, detailed_position, enter_time, "NULL", yellow_card, red_card);
                            }
                            else {
                                System.out.printf("| %-20s | %-20s | %20s | %20s | %20s | %20s | %20s |%n", player_name, shirt_number, detailed_position, enter_time, leaving_time, yellow_card, red_card);
                            }
                        }
                    } catch (SQLException e){
                        System.out.println(e);
                    }

                    System.out.println("");

                    // TODO: List all players of the team that have not yet been selected
                    System.out.println("Possible players from " + query2_country + " not yet selected:");
                    System.out.println("");

                    System.out.printf("| %-20s | %-20s | %20s |%n", "Player Name", "Shirt Number", "Position");

                    String free_players = "SELECT player.name, player.shirtnumber, player.position\n" +
                            "FROM player, teammember\n" +
                            "Where player.emailaddress = teammember.emailaddress AND teammember.country = '" + query2_country + "' AND player.emailaddress NOT IN (\n" +
                            "SELECT playerperformance.emailaddress\n" +
                            "FROM playerperformance\n" +
                            "WHERE teammember.country = '" + query2_country +"' AND playerperformance.matchid = " + query2_match_id +")";

                    try {
                        ResultSet rs = statement.executeQuery(free_players);

                        while(rs.next()){
                            String player_name = rs.getString("name");
                            int shirt_number = rs.getInt("shirtnumber");
                            String position = rs.getString("position");

                            System.out.println("");
                            System.out.printf("| %-20s | %-20s | %20s |%n", player_name, shirt_number, position);
                        }
                    } catch (SQLException e){
                        System.out.println(e);
                    }

                    System.out.println("");

                    //TODO: Take the player number and specific position and add to database
                    if (players_added >= 3){
                        System.out.println("ERROR!!!: The team already has the maximum players (3) allowed for this match.");
                        continue;
                    }

                    System.out.print("Enter the shirt number of the player you want to insert or [P] to go to the previous menu: ");
                    String player_number = sc.nextLine();

                    if (player_number.equals("P")){
                        continue;
                    }

                    System.out.print("Enter the specific position of the player (ST, CM, CB, etc): ");
                    String player_position = sc.nextLine();

                    if (player_position.equals("P")){
                        continue;
                    }

                    int latest_performance_id = 0;

                    try {
                        ResultSet rs = statement.executeQuery("SELECT MAX(performanceid) AS pid FROM PlayerPerformance");

                        while(rs.next()) {
                            latest_performance_id = rs.getInt("pid");
                        }
                    } catch (SQLException e){
                        System.out.println(e);
                    }

                    List<String> emails = new ArrayList<String>();

                    String query = "SELECT player.emailaddress\n" +
                            "From player, teammember\n" +
                            "WHERE shirtnumber = " + player_number +" AND teammember.emailaddress = player.emailaddress AND teammember.country = '" + query2_country + "'";

                    System.out.println(query);

                    try {
                        ResultSet rs = statement.executeQuery(query);

                        while(rs.next()) {
                            String email_id = rs.getString("emailAddress");
                            emails.add(email_id);
                            System.out.println(email_id);
                        }

                    } catch (SQLException e){
                        System.out.println(e);
                    }

                    try {
                        System.out.println("Entering player " + player_number + " with position " + player_position + " for match " + query2_match_id);
                        latest_performance_id = latest_performance_id + 1;
                        PreparedStatement psql = conn.prepareStatement("INSERT INTO PlayerPerformance (performanceid, redCard, yellowCard, detailedPosition, enterTime, leavingTime, startingStatus, emailAddress, matchId) VALUES (?, 0, 0, ?, 0, NULL, 'starting XI', ?, ?)");
                        psql.setInt(1, latest_performance_id);
                        psql.setString(2, player_position);
                        psql.setString(3, emails.get(0));
                        psql.setInt(4, Integer.parseInt(query2_match_id));

//                        System.out.println(latest_performance_id + " --- " + player_position + " --- " + emails.get(0) + " --- " + query2_match_id);


                        psql.executeUpdate();

                    } catch (SQLException e){
                        System.out.println(e);
                    }

                System.out.println("");
                System.out.println("The following players from " + query2_country + " are already entered for match " + query2_match_id + ":");
                System.out.println("");

                System.out.println("");
                System.out.printf("| %-20s | %-20s | %20s | %20s | %20s | %20s | %20s |%n", "Player Name", "Shirt Number", "Detailed Position", "From Minute", "To Minute", "Yellow Cards", "Red Cards");

                try {
                    ResultSet rs = statement.executeQuery(registered_players);

                    while(rs.next()){

                        String player_name = rs.getString("name");
                        int shirt_number = rs.getInt("shirtnumber");
                        String detailed_position = rs.getString("detailedposition");
                        int enter_time = rs.getInt("entertime");
                        int leaving_time = rs.getInt("leavingtime");
                        int yellow_card = rs.getInt("yellowcard");
                        int red_card = rs.getInt("redcard");

                        System.out.println("");

                        if (leaving_time == 0){
                            System.out.printf("| %-20s | %-20s | %20s | %20s | %20s | %20s | %20s |%n", player_name, shirt_number, detailed_position, enter_time, "NULL", yellow_card, red_card);
                        }
                        else {
                            System.out.printf("| %-20s | %-20s | %20s | %20s | %20s | %20s | %20s |%n", player_name, shirt_number, detailed_position, enter_time, leaving_time, yellow_card, red_card);
                        }
                    }
                } catch (SQLException e){
                    System.out.println(e);
                }

                System.out.println("");

                // TODO: List all players of the team that have not yet been selected
                System.out.println("Possible players from " + query2_country + " not yet selected:");
                System.out.println("");

                System.out.printf("| %-20s | %-20s | %20s |%n", "Player Name", "Shirt Number", "Position");

                try {
                    ResultSet rs = statement.executeQuery(free_players);

                    while(rs.next()){
                        String player_name = rs.getString("name");
                        int shirt_number = rs.getInt("shirtnumber");
                        String position = rs.getString("position");

                        System.out.println("");
                        System.out.printf("| %-20s | %-20s | %20s |%n", player_name, shirt_number, position);
                    }
                } catch (SQLException e){
                    System.out.println(e);
                }

                System.out.println("");

                    continue;
            }



            // MENU ITEM 3 ////////////////////////////////////////////////////////////////////////////////////////////

            else if (menu_code == 3){
                System.out.println("");
                System.out.println("Upcoming matches for this World Cup: ");
                System.out.println("");

                System.out.printf("| %-20s | %-20s | %20s | %20s |%n", "Match ID", "Home Team", "Away Team", "Date");

                String menu3_query = "SELECT pt1.matchid, pt1.country AS home_team, pt2.country AS away_team , match.time AS DATE\n" +
                        "FROM playingteam AS pt1 JOIN playingteam AS pt2 ON pt1.matchid = pt2.matchid, match\n" +
                        "WHERE pt1.status = 'Home' AND pt2.status = 'Away'AND match.matchid = pt1.matchid AND match.time >= CURRENT_TIMESTAMP ORDER BY pt1.matchid";

                try {
                    ResultSet rs = statement.executeQuery(menu3_query);

                    while(rs.next()){
                        String mid = rs.getString("matchid");
                        String home_team = rs.getString("home_team");
                        String away_team = rs.getString("away_team");
                        String date = rs.getTimestamp("date").toString();

                        System.out.println("");
                        System.out.printf("| %-20s | %-20s | %20s | %20s |%n", mid, home_team, away_team, date);
                    }
                } catch (SQLException e){
                    System.out.println(e);
                }

                System.out.println("");
                System.out.println("NOTE: To go to previous menu, press [P]");
                System.out.print("Please enter the match ID of the match you wish to buy tickets for: ");
                String query3_match_id = sc.nextLine();

                if (query3_match_id.equals("P")){
                    continue;
                }

                System.out.println("");
                System.out.println("NOTE: To go to previous menu, press [P]");
                System.out.print("Please enter the number of tickets you would like to purchase: ");
                String query3_tickets = sc.nextLine();

                if (query3_tickets.equals("P")){
                    continue;
                }

                // I fetch latest order ID and latest ticket ID

                int latest_order_id = 0;

                try {
                    ResultSet rs = statement.executeQuery("SELECT MAX(orderid) AS oid FROM Orders");

                    while(rs.next()) {
                        latest_order_id = rs.getInt("oid");
                    }
                } catch (SQLException e){
                    System.out.println(e);
                }

                int latest_ticket_id = 0;

                try {
                    ResultSet rs = statement.executeQuery("SELECT MAX(ticketid) AS tid FROM Ticket");

                    while(rs.next()) {
                        latest_ticket_id = rs.getInt("tid");
                    }
                } catch (SQLException e){
                    System.out.println(e);
                }

                try {

                    System.out.println("Purchasing " + query3_tickets + " tickets for match " + query3_match_id);
                    latest_ticket_id = latest_ticket_id + 1;

                    final Random random = new Random();
                    final int millisInDay = 24*60*60*1000;
                    Time time = new Time((long)random.nextInt(millisInDay));

                    Random r = new Random();
                    int low = 50;
                    int high = 100;
                    int price = r.nextInt(high-low) + low;

                    PreparedStatement psql = conn.prepareStatement("INSERT INTO Ticket (ticketid, price, gateopening) VALUES (?, ?, ?)");
                    psql.setInt(1, latest_ticket_id);
                    psql.setInt(2, price);
                    psql.setTime(3, time);
                    psql.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(e);
                }

                try {
                    latest_order_id = latest_order_id + 1;

                    java.sql.Date sqlDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

                    PreparedStatement psql = conn.prepareStatement("INSERT INTO Orders (orderid, orderdate, numberoftickets) VALUES (?, ?, ?)");
                    psql.setInt(1, latest_order_id);
                    psql.setDate(2, sqlDate);
                    psql.setInt(3, Integer.parseInt(query3_tickets));
                    psql.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(e);
                }

                try {
                    PreparedStatement psql = conn.prepareStatement("INSERT INTO TicketOrders (ticketid, orderid) VALUES (?, ?)");
                    psql.setInt(1, latest_ticket_id);
                    psql.setInt(2, latest_order_id);
                    psql.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(e);
                }

                try {
                    PreparedStatement psql = conn.prepareStatement("INSERT INTO MatchTicket (matchid, ticketid) VALUES (?, ?)");
                    psql.setInt(1, Integer.parseInt(query3_match_id));
                    psql.setInt(2, latest_ticket_id);
                    psql.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(e);
                }

                try {
                    ResultSet rs = statement.executeQuery("SELECT * From Orders WHERE orderid = " + latest_order_id);

                    while(rs.next()){
                        int order_id = rs.getInt("orderid");
                        String order_date = rs.getDate("orderdate").toString();
                        int order_tickets = rs.getInt("numberoftickets");

                        System.out.println("");
                        System.out.println("#############################################################################################");
                        System.out.println("SUCCESS! Your order of " + order_tickets + " tickets for match " + query3_match_id + " has been confirmed on " + order_date + ". Order ID: " + order_id);
                        System.out.println("#############################################################################################");
                    }
                } catch (SQLException e){
                    System.out.println(e);
                }

                continue menu;
            }

            else if (menu_code == 4){
                System.out.println("Exiting Application...");
                sc.close();
                statement.close();
                conn.close();
		        System.out.println("Connection to server closed successfully!");
                break menu;
            }

            else {
                System.out.println("Input Error: Please enter a number between 1 and 4");
                continue menu;
            }
        }
    }
}
