import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RoutingTests {

    @Test
    public void can_find_routes() {
        Point start = new Point(1, 2);
        Point end = new Point(3, 5);
        int xLimit = 5;
        int yLimit = 6;
        List<List<Point>> routes = Routing.findRoutes(start, end, xLimit, yLimit);
        assertThat(routes.size(), is(216));
    }
}