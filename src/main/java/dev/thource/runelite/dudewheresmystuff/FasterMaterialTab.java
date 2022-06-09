package dev.thource.runelite.dudewheresmystuff;

import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BooleanSupplier;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.ColorScheme;

/**
 * This class represents a Material Design inspired tab.
 *
 * <p>Each tab will communicate with it's containing group when it's clicked and that group will
 * display the tab's content on it's own display.
 *
 * @author Psikoi
 */
public class FasterMaterialTab extends JLabel {
  private static final Border SELECTED_BORDER =
      new CompoundBorder(
          BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.BRAND_ORANGE),
          BorderFactory.createEmptyBorder(5, 10, 4, 10));

  private static final Border UNSELECTED_BORDER = BorderFactory.createEmptyBorder(5, 10, 5, 10);

  /* The tab's associated content display */
  @Getter private final JComponent content;

  /* To be execuded when the tab is selected */
  @Setter private transient BooleanSupplier onSelectEvent;

  @Getter private boolean selected;

  public FasterMaterialTab(String string, FasterMaterialTabGroup group, JComponent content) {
    super(string);

    this.content = content;

    if (selected) {
      select();
    } else {
      unselect();
    }

    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent mouseEvent) {
            group.select(FasterMaterialTab.this);
          }
        });

    if (!Strings.isNullOrEmpty(string)) {
      addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
              FasterMaterialTab tab = (FasterMaterialTab) e.getSource();
              tab.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
              FasterMaterialTab tab = (FasterMaterialTab) e.getSource();
              if (!tab.isSelected()) {
                tab.setForeground(Color.GRAY);
              }
            }
          });
    }
  }

  public FasterMaterialTab(ImageIcon icon, FasterMaterialTabGroup group, JComponent content) {
    this("", group, content);
    setIcon(icon);
    setOpaque(true);
    setVerticalAlignment(SwingConstants.CENTER);
    setHorizontalAlignment(SwingConstants.CENTER);
    setBackground(ColorScheme.DARKER_GRAY_COLOR);

    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseEntered(MouseEvent e) {
            FasterMaterialTab tab = (FasterMaterialTab) e.getSource();
            tab.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
          }

          @Override
          public void mouseExited(MouseEvent e) {
            FasterMaterialTab tab = (FasterMaterialTab) e.getSource();
            tab.setBackground(ColorScheme.DARKER_GRAY_COLOR);
          }
        });
  }

  public boolean select() {
    if (onSelectEvent != null && !onSelectEvent.getAsBoolean()) {
      return false;
    }

    setBorder(SELECTED_BORDER);
    setForeground(Color.WHITE);
    selected = true;
    return true;
  }

  public void unselect() {
    setBorder(UNSELECTED_BORDER);
    setForeground(Color.GRAY);
    selected = false;
  }
}