import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.vaadin.hezamu.canvas.Canvas;

import javax.servlet.annotation.WebServlet;
import java.util.List;

@Theme("mytheme")
public class App extends UI {
    private static final int GRID_X_START = 30;
    private static final int GRID_Y_START = 30;
    private static final int GRID_X_GRID = 80;
    private static final int GRID_Y_GRID = 80;

    private TextField textFieldGridXLimit = new TextField("Grid X limit");
    private TextField textFieldGridYLimit = new TextField("Grid Y limit");
    private TextField textFieldStartX = new TextField("Start point X");
    private TextField textFieldStartY = new TextField("Start point Y");
    private TextField textFieldDestinationX = new TextField("End point X");
    private TextField textFieldDestinationY = new TextField("End point Y");
    private Canvas canvas = new Canvas();
    private Table table;

    private int gridXLimit;
    private int gridYLimit;
    private int startX;
    private int startY;
    private int destinationX;
    private int destinationY;
    private List<List<Point>> routes;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        TextField[] textFields = {textFieldGridXLimit, textFieldGridYLimit, textFieldStartX, textFieldStartY,
                textFieldDestinationX, textFieldDestinationY};

        textFieldGridXLimit.setValue("5");
        textFieldGridXLimit.addValueChangeListener(e -> update());

        textFieldGridYLimit.setValue("6");
        textFieldGridYLimit.addValueChangeListener(e -> update());

        textFieldStartX.setValue("1");
        textFieldStartX.addValueChangeListener(e -> update());

        textFieldStartY.setValue("2");
        textFieldStartY.addValueChangeListener(e -> update());

        textFieldDestinationX.setValue("3");
        textFieldDestinationX.addValueChangeListener(e -> update());

        textFieldDestinationY.setValue("5");
        textFieldDestinationY.addValueChangeListener(e -> update());

        for (TextField textField : textFields) {
            textField.setColumns(2);
            textField.setRequired(true);
        }

        FormLayout formLayout1 = new FormLayout();
        formLayout1.addComponents(textFieldGridXLimit, textFieldStartX, textFieldDestinationX);
        FormLayout formLayout2 = new FormLayout();
        formLayout2.addComponents(textFieldGridYLimit, textFieldStartY, textFieldDestinationY);
        GridLayout gridLayout1 = new GridLayout(4, 8);
        //gridLayout1.setSpacing(true);
        gridLayout1.setMargin(true);
        gridLayout1.addComponents(formLayout1, formLayout2);

        table = new Table("Routes");
        table.addContainerProperty("No.", Integer.class, null);
        table.addContainerProperty("Route", String.class, null);
        table.setWidth("400px");
        table.setSelectable(true);
        table.setImmediate(true);

        table.addValueChangeListener((Property.ValueChangeListener) event -> {
            if (table.getValue() != null) {
                String value = table.getValue().toString();
                int selected = Integer.valueOf(value) - 3;
                drawPoints(routes.get(selected));
            }
        });

        table.setPageLength(table.size());
        gridLayout1.addComponent(table, 0, 2, 1, 7);
        gridLayout1.addComponent(canvas, 2, 0, 3, 7);
        setValues();
        canvas.setWidth("600px");
        canvas.setHeight("600px");
        update();
        setContent(gridLayout1);
    }

    private void setValues() {
        this.gridXLimit = Integer.valueOf(textFieldGridXLimit.getValue());
        this.gridYLimit = Integer.valueOf(textFieldGridYLimit.getValue());
        this.startX = Integer.valueOf(textFieldStartX.getValue());
        this.startY = Integer.valueOf(textFieldStartY.getValue());
        this.destinationX = Integer.valueOf(textFieldDestinationX.getValue());
        this.destinationY = Integer.valueOf(textFieldDestinationY.getValue());
        if (startX > gridXLimit) {
            startX = gridXLimit;
        }
        if (startY > gridYLimit) {
            startY = gridXLimit;
        }
    }

    private void update() {
        setValues();
        drawGrid();
        drawStartDestinationPoints();
        Point start = new Point(startX, startY);
        Point destination = new Point(destinationX, destinationY);
        this.routes = Routing.findRoutes(start, destination, this.gridXLimit, this.gridYLimit);
        table.removeAllItems();
        int i = 1;
        for (List<Point> list : routes) {
            table.addItem(new Object[]{i++, list.toString()}, i + 1);
        }
        table.setPageLength(10);
    }

    private void drawStartDestinationPoints() {
        canvas.saveContext();
        int x = GRID_X_START + this.startX * GRID_X_GRID - 4;
        int y = GRID_Y_START + this.startY * GRID_Y_GRID - 4;
        canvas.moveTo(x, y);
        canvas.setFillStyle("rgb255, 0, 0)");
        canvas.fillRect(x, y, 8, 8);

        x = GRID_X_START + this.destinationX * GRID_X_GRID - 4;
        y = GRID_Y_START + this.destinationY * GRID_Y_GRID - 4;
        canvas.moveTo(x, y);
        canvas.setFillStyle("rgb(4, 48, 244)");
        canvas.fillRect(x, y, 8, 8);
        canvas.restoreContext();
    }

    private void drawGrid() {
        canvas.saveContext();
        canvas.clear();
        //canvas.setStrokeStyle("rgb(5, 5, 5)");

        canvas.beginPath();
        canvas.setLineWidth(1);
        for (int i = 0; i <= gridXLimit; i++) {
            canvas.moveTo(GRID_X_START + i * GRID_X_GRID, GRID_Y_START);
            canvas.lineTo(GRID_X_START + i * GRID_X_GRID, GRID_Y_START + gridYLimit * GRID_Y_GRID);
        }
        for (int i = 0; i <= gridYLimit; i++) {
            canvas.moveTo(GRID_X_START, GRID_Y_START + i * GRID_Y_GRID);
            canvas.lineTo(GRID_X_START + gridXLimit * GRID_X_GRID, GRID_Y_START + i * GRID_Y_GRID);
        }
        canvas.stroke();
        canvas.closePath();
        canvas.restoreContext();
    }

    private void drawPoints(List<Point> points) {
        drawGrid();
        drawStartDestinationPoints();

        canvas.saveContext();
        canvas.setStrokeStyle("rgb(255, 5, 5)");

        canvas.beginPath();
        canvas.setLineWidth(3);
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            int x = GRID_X_START + point.getX() * GRID_X_GRID;
            int y = GRID_Y_START + point.getY() * GRID_Y_GRID;
            if (i == 0) {
                canvas.moveTo(x, y);
            } else {
                canvas.lineTo(x, y);
            }
        }
        canvas.stroke();
        canvas.closePath();
        canvas.restoreContext();
    }


    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = App.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
