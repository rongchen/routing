import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Routing {
    private int xLimit;
    private int yLimit;
    private Point end;
    private List<List<Point>> routes;

    public Routing(int xLimit, int yLimit, Point end) {
        this.xLimit = xLimit;
        this.yLimit = yLimit;
        this.end = end;
        this.routes = new ArrayList<>();
    }

    public static List<List<Point>> findRoutes(Point start, Point destination, int xLimit, int yLimit) {
        Routing routing = new Routing(xLimit, yLimit, destination);
        routing.findRoutes(Collections.singletonList(start));
        return routing.routes;
    }

    private void findRoutes(List<Point> current) {
        Point last = current.get(current.size() - 1);
        if(last.equals(end)) { // destination reached
            this.routes.add(current);
            return;
        }
        if(last.getX() != 0) {
            Point next = new Point(last.getX() - 1, last.getY()); // one step left
            proceedIfNotVisited(current, next);
        }
        if(last.getX() != xLimit) {
            Point next = new Point(last.getX() + 1, last.getY()); // on step right
            proceedIfNotVisited(current, next);
        }
        if(last.getY() != yLimit) {
            Point next = new Point(last.getX(), last.getY() + 1); // on step down
            proceedIfNotVisited(current, next);
        }
    }

    private void proceedIfNotVisited(List<Point> visited, Point possibleNext) {
        if(! visited.contains(possibleNext)) {
            List<Point> currentList = new ArrayList<>(visited);
            currentList.add(possibleNext);
            findRoutes(currentList);
        }
    }
}
