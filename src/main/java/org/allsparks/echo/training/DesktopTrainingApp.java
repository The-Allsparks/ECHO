package org.allsparks.echo.training;

import org.allsparks.echo.EchoEngine;
import org.allsparks.echo.EchoFeatureFlags;
import org.allsparks.echo.clock.FakeClock;
import org.allsparks.echo.config.EchoConfig;
import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.input.AudioDeviceStatus;
import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.observe.EchoDecisionRecord;
import org.allsparks.echo.render.CueRenderer;
import org.allsparks.echo.render.DesktopToneRenderer;
import org.allsparks.echo.render.FakeRenderer;
import org.allsparks.echo.value.Scalar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

/**
 * Off-field trainer. Audio is off unless {@code --audio} is passed.
 * This is not Driver Hub or competition software.
 */
public final class DesktopTrainingApp {
    public static void main(String[] args) {
        boolean audio = false;
        for (String arg : args) {
            if ("--audio".equals(arg)) {
                audio = true;
            }
        }
        boolean play = audio;
        SwingUtilities.invokeLater(() -> new DesktopTrainingApp(play).show());
    }

    private final FakeClock clock = new FakeClock();
    private final TrainingMetrics metrics = new TrainingMetrics();
    private final EchoEngine engine;
    private final TargetPanel targetPanel = new TargetPanel();
    private final JTextArea explanation = new JTextArea(8, 60);
    private final JSlider bearing = new JSlider(-180, 180, 0);
    private final JSlider distance = new JSlider(10, 300, 100);
    private final JSlider confidence = new JSlider(0, 100, 90);
    private final JSlider age = new JSlider(0, 500, 0);
    private final JCheckBox enabled = new JCheckBox("Driver enable", true);
    private EchoDecisionRecord last;

    private DesktopTrainingApp(boolean audio) {
        CueRenderer renderer = audio
                ? new DesktopToneRenderer(true)
                : new FakeRenderer();
        EchoFeatureFlags flags = EchoFeatureFlags.disabled();
        this.engine = new EchoEngine(clock, EchoConfig.defaults(), flags, renderer);
    }

    private void show() {
        JFrame frame = new JFrame("ECHO desktop training (not competition)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel controls = new JPanel(new GridLayout(0, 1));
        controls.add(labeled("Bearing deg (+right)", bearing));
        controls.add(labeled("Distance cm", distance));
        controls.add(labeled("Confidence %", confidence));
        controls.add(labeled("Observation age ms", age));
        controls.add(enabled);
        JButton mute = new JButton("Mute / disable");
        mute.addActionListener(e -> {
            enabled.setSelected(false);
            engine.mute();
            tick();
        });
        controls.add(mute);
        explanation.setEditable(false);
        explanation.setLineWrap(true);
        frame.add(targetPanel, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.EAST);
        frame.add(explanation, BorderLayout.SOUTH);
        bearing.addChangeListener(e -> tick());
        distance.addChangeListener(e -> tick());
        confidence.addChangeListener(e -> tick());
        age.addChangeListener(e -> tick());
        enabled.addChangeListener(e -> tick());
        new Timer(200, e -> tick()).start();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        tick();
    }

    private void tick() {
        clock.advanceMs(20);
        long now = clock.nanoTime();
        EchoSnapshot snap = Snapshots.guidance(
                clock,
                Math.toRadians(bearing.getValue()),
                distance.getValue() / 100.0,
                confidence.getValue() / 100.0)
                .toBuilder()
                .driverEnabled(enabled.isSelected())
                .receiptNanos(now)
                .observationNanos(now - age.getValue() * 1_000_000L)
                .build();
        last = engine.step(snap).record();
        targetPanel.bearingDeg = bearing.getValue();
        targetPanel.family = last.selected();
        targetPanel.repaint();
        explanation.setText(last.toExplanation()
                + "\nmetrics dirAcc=" + metrics.directionAccuracy()
                + "\nTHIS IS NOT A DRIVER HUB TEST.");
    }

    private static JPanel labeled(String title, JSlider slider) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(slider, BorderLayout.CENTER);
        return p;
    }

    private static final class TargetPanel extends JPanel {
        int bearingDeg;
        CueFamily family = CueFamily.SILENCE;

        TargetPanel() {
            setPreferredSize(new Dimension(420, 420));
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createTitledBorder("Selected target (visual = audio mapping)"));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            g.setColor(Color.DARK_GRAY);
            g.drawOval(cx - 150, cy - 150, 300, 300);
            g.setColor(family == CueFamily.GUIDANCE ? Color.CYAN : Color.GRAY);
            double rad = Math.toRadians(bearingDeg);
            int x = cx + (int) (Math.sin(rad) * 140);
            int y = cy - (int) (Math.cos(rad) * 140);
            g.fillOval(x - 8, y - 8, 16, 16);
            g.setColor(Color.WHITE);
            g.drawString("forward", cx - 20, cy - 160);
            g.drawString("cue " + family, 12, 20);
        }
    }
}
