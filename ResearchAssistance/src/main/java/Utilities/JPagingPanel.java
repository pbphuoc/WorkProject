package Utilities;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//http://terai.xrea.jp/Swing/TablePagination.html
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import javax.swing.text.*;

public class JPagingPanel extends JPanel {

    private static final Color evenColor = new Color(240, 255, 250);
    private static final LinkViewRadioButtonUI ui = new LinkViewRadioButtonUI();
    private static int LR_PAGE_SIZE = 5;
    private final Box box = Box.createHorizontalBox();
    private DefaultTableModel model;
    private final TableRowSorter<TableModel> sorter;
    private JTable table;

    public JPagingPanel(DefaultTableModel model) {
        super(new BorderLayout());

        this.model = model;
        this.table = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer tcr, int row, int column) {
                ((DefaultTableCellRenderer) tcr).setHorizontalAlignment(JLabel.LEFT);
                Component c = super.prepareRenderer(tcr, row, column);
                if (isRowSelected(row)) {
                    c.setForeground(getSelectionForeground());
                    c.setBackground(getSelectionBackground());
                } else {
                    c.setForeground(getForeground());
                    c.setBackground((row % 2 == 0) ? evenColor : getBackground());
                }
                return c;
            }
        };
        sorter = new TableRowSorter<TableModel>(model);
        table.setModel(this.model);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension());
        table.setShowGrid(false);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        table.setRowSorter(sorter);

        initLinkBox(10, 1);
        box.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        add(box, BorderLayout.NORTH);
        add(new JScrollPane(table));
        setPreferredSize(new Dimension(320, 240));
    }

    public void initLinkBox(final int itemsPerPage, final int currentPageIndex) {
        //assert currentPageIndex>0;
        sorter.setRowFilter(new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                int ti = currentPageIndex - 1;
                int ei = entry.getIdentifier();
                return ti * itemsPerPage <= ei && ei < ti * itemsPerPage + itemsPerPage;
            }
        });

        int startPageIndex = currentPageIndex - LR_PAGE_SIZE;
        if (startPageIndex <= 0) {
            startPageIndex = 1;
        }

//#if 0 //BUG
        //int maxPageIndex = (model.getRowCount()/itemsPerPage)+1;
//#else
        /* "maxPageIndex" gives one blank page if the module of the division is not zero.
         *   pointed out by erServi
         * e.g. rowCount=100, maxPageIndex=100
         */
        int rowCount = model.getRowCount();
        int v = rowCount % itemsPerPage == 0 ? 0 : 1;
        int maxPageIndex = rowCount / itemsPerPage + v;
//#endif
        int endPageIndex = currentPageIndex + LR_PAGE_SIZE - 1;
        if (endPageIndex > maxPageIndex) {
            endPageIndex = maxPageIndex;
        }

        box.removeAll();
        if (startPageIndex >= endPageIndex) {
            //if I only have one page, Y don't want to see pagination buttons
            //suggested by erServi
            return;
        }

        ButtonGroup bg = new ButtonGroup();
        JRadioButton f = makePrevNextRadioButton(itemsPerPage, 1, "First", currentPageIndex > 1);
        box.add(f);
        bg.add(f);
        JRadioButton p = makePrevNextRadioButton(itemsPerPage, currentPageIndex - 1, "Previous", currentPageIndex > 1);
        box.add(p);
        bg.add(p);
        box.add(Box.createHorizontalGlue());
        for (int i = startPageIndex; i <= endPageIndex; i++) {
            JRadioButton c = makeRadioButton(itemsPerPage, currentPageIndex, i);
            box.add(c);
            bg.add(c);
        }
        box.add(Box.createHorizontalGlue());
        JRadioButton n = makePrevNextRadioButton(itemsPerPage, currentPageIndex + 1, "Next", currentPageIndex < maxPageIndex);
        box.add(n);
        bg.add(n);
        JRadioButton l = makePrevNextRadioButton(itemsPerPage, maxPageIndex, "Last", currentPageIndex < maxPageIndex);
        box.add(l);
        bg.add(l);
        box.revalidate();
        box.repaint();
    }

    public JTable getTable() {
        return table;
    }

    public void setModel(DefaultTableModel model) {
        this.model = model;
    }

    private JRadioButton makeRadioButton(final int itemsPerPage, int current, final int target) {
        JRadioButton radio = new JRadioButton(String.valueOf(target)) {
            @Override
            protected void fireStateChanged() {
                ButtonModel model = getModel();
                if (!model.isEnabled()) {
                    setForeground(Color.GRAY);
                } else if (model.isPressed() && model.isArmed()) {
                    setForeground(Color.GREEN);
                } else if (model.isSelected()) {
                    setForeground(Color.RED);
                    //}else if(isRolloverEnabled() && model.isRollover()) {
                    //    setForeground(Color.BLUE);
                }
                super.fireStateChanged();
            }
        };
        radio.setForeground(Color.BLUE);
        radio.setUI(ui);
        if (target == current) {
            radio.setSelected(true);
        }
        radio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initLinkBox(itemsPerPage, target);
            }
        });
        return radio;
    }

    private JRadioButton makePrevNextRadioButton(final int itemsPerPage, final int target, String title, boolean flag) {
        JRadioButton radio = new JRadioButton(title);
        radio.setForeground(Color.BLUE);
        radio.setUI(ui);
        radio.setEnabled(flag);
        radio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initLinkBox(itemsPerPage, target);
            }
        });
        return radio;
    }
}

class LinkViewRadioButtonUI extends BasicRadioButtonUI {

    @Override
    public Icon getDefaultIcon() {
        return null;
    }
    private static Dimension size = new Dimension();
    private static Rectangle viewRect = new Rectangle();
    private static Rectangle iconRect = new Rectangle();
    private static Rectangle textRect = new Rectangle();

    @Override
    public synchronized void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        Font f = c.getFont();
        g.setFont(f);
        FontMetrics fm = c.getFontMetrics(f);

        Insets i = c.getInsets();
        size = b.getSize(size);
        viewRect.x = i.right;
        viewRect.y = i.top;
        viewRect.width = size.width - i.right - viewRect.x;
        viewRect.height = size.height - i.bottom - viewRect.y;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        textRect.x = textRect.y = textRect.width = textRect.height = 0;

        String text = SwingUtilities.layoutCompoundLabel(
                c, fm, b.getText(), null, //altIcon != null ? altIcon : getDefaultIcon(),
                b.getVerticalAlignment(), b.getHorizontalAlignment(),
                b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                viewRect, iconRect, textRect,
                0); //b.getText() == null ? 0 : b.getIconTextGap());

        if (c.isOpaque()) {
            g.setColor(b.getBackground());
            g.fillRect(0, 0, size.width, size.height);
        }
        if (text == null) {
            return;
        }

        g.setColor(b.getForeground());
        if (!model.isSelected() && !model.isPressed() && !model.isArmed()
                && b.isRolloverEnabled() && model.isRollover()) {
            g.drawLine(viewRect.x, viewRect.y + viewRect.height,
                    viewRect.x + viewRect.width, viewRect.y + viewRect.height);
        }
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            v.paint(g, textRect);
        } else {
            paintText(g, b, textRect, text);
        }
    }
}
