/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.nurserostering.swingui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.NurseRosterParametrization;
import org.optaplanner.examples.nurserostering.domain.Shift;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;

public class NurseRosteringPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/nurserostering/swingui/nurseRosteringLogo.png";

    private final ImageIcon employeeIcon;
    private final ImageIcon deleteEmployeeIcon;

    private JPanel employeeListPanel;

    private JTextField planningWindowStartField;
    private AbstractAction advancePlanningWindowStartAction;
    private EmployeePanel unassignedPanel;
    private Map<Employee, EmployeePanel> employeeToPanelMap;

    public NurseRosteringPanel() {
        employeeIcon = new ImageIcon(getClass().getResource("employee.png"));
        deleteEmployeeIcon = new ImageIcon(getClass().getResource("deleteEmployee.png"));
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        createEmployeeListPanel();
        JPanel headerPanel = createHeaderPanel();
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(headerPanel).addComponent(employeeListPanel));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addComponent(employeeListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE));
    }

    public ImageIcon getEmployeeIcon() {
        return employeeIcon;
    }

    public ImageIcon getDeleteEmployeeIcon() {
        return deleteEmployeeIcon;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        JPanel planningWindowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        planningWindowPanel.add(new JLabel("Planning window start:"));
        planningWindowStartField = new JTextField(10);
        planningWindowStartField.setEditable(false);
        planningWindowPanel.add(planningWindowStartField);
        advancePlanningWindowStartAction = new AbstractAction("Advance 1 day into the future") {
            @Override
            public void actionPerformed(ActionEvent e) {
                advancePlanningWindowStart();
            }
        };
        advancePlanningWindowStartAction.setEnabled(false);
        planningWindowPanel.add(new JButton(advancePlanningWindowStartAction));
        headerPanel.add(planningWindowPanel, BorderLayout.WEST);
        JLabel shiftTypeExplanation = new JLabel("E = Early shift, L = Late shift, ...");
        headerPanel.add(shiftTypeExplanation, BorderLayout.CENTER);
        return headerPanel;
    }

    private void createEmployeeListPanel() {
        employeeListPanel = new JPanel();
        employeeListPanel.setLayout(new BoxLayout(employeeListPanel, BoxLayout.Y_AXIS));
        unassignedPanel = new EmployeePanel(this, Collections.<ShiftDate>emptyList(), Collections.<Shift>emptyList(),
                null);
        employeeListPanel.add(unassignedPanel);
        employeeToPanelMap = new LinkedHashMap<Employee, EmployeePanel>();
        employeeToPanelMap.put(null, unassignedPanel);
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public NurseRoster getNurseRoster() {
        return (NurseRoster) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        NurseRoster nurseRoster = (NurseRoster) solution;
        for (EmployeePanel employeePanel : employeeToPanelMap.values()) {
            if (employeePanel.getEmployee() != null) {
                employeeListPanel.remove(employeePanel);
            }
        }
        employeeToPanelMap.clear();
        employeeToPanelMap.put(null, unassignedPanel);
        unassignedPanel.clearShiftAssignments();
        List<ShiftDate> shiftDateList = nurseRoster.getShiftDateList();
        List<Shift> shiftList = nurseRoster.getShiftList();
        unassignedPanel.setShiftDateListAndShiftList(shiftDateList, shiftList);
        updatePanel(nurseRoster);
        advancePlanningWindowStartAction.setEnabled(true);
        planningWindowStartField.setText(nurseRoster.getNurseRosterParametrization().getPlanningWindowStart().getLabel());
    }

    @Override
    public void updatePanel(Solution solution) {
        NurseRoster nurseRoster = (NurseRoster) solution;
        List<ShiftDate> shiftDateList = nurseRoster.getShiftDateList();
        List<Shift> shiftList = nurseRoster.getShiftList();
        Set<Employee> deadEmployeeSet = new LinkedHashSet<Employee>(employeeToPanelMap.keySet());
        deadEmployeeSet.remove(null);
        for (Employee employee : nurseRoster.getEmployeeList()) {
            deadEmployeeSet.remove(employee);
            EmployeePanel employeePanel = employeeToPanelMap.get(employee);
            if (employeePanel == null) {
                employeePanel = new EmployeePanel(this, shiftDateList, shiftList, employee);
                employeeListPanel.add(employeePanel);
                employeeToPanelMap.put(employee, employeePanel);
            }
            employeePanel.clearShiftAssignments();
        }
        unassignedPanel.clearShiftAssignments();
        for (ShiftAssignment shiftAssignment : nurseRoster.getShiftAssignmentList()) {
            Employee employee = shiftAssignment.getEmployee();
            EmployeePanel employeePanel = employeeToPanelMap.get(employee);
            employeePanel.addShiftAssignment(shiftAssignment);
        }
        for (Employee deadEmployee : deadEmployeeSet) {
            EmployeePanel deadEmployeePanel = employeeToPanelMap.remove(deadEmployee);
            employeeListPanel.remove(deadEmployeePanel);
        }
        for (EmployeePanel employeePanel : employeeToPanelMap.values()) {
            employeePanel.update();
        }
    }

    private void advancePlanningWindowStart() {
        logger.info("Advancing planningWindowStart.");
        if (solutionBusiness.isSolving()) {
            JOptionPane.showMessageDialog(this.getTopLevelAncestor(),
                    "The GUI does not support this action yet during solving.\nOptaPlanner itself does support it.\n"
                    + "\nTerminate solving first and try again.",
                    "Unsupported in GUI", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                NurseRoster nurseRoster = (NurseRoster) scoreDirector.getWorkingSolution();
                NurseRosterParametrization nurseRosterParametrization = nurseRoster.getNurseRosterParametrization();
                List<ShiftDate> shiftDateList = nurseRoster.getShiftDateList();
                ShiftDate planningWindowStart = nurseRosterParametrization.getPlanningWindowStart();
                int windowStartIndex = shiftDateList.indexOf(planningWindowStart);
                if (windowStartIndex < 0) {
                    throw new IllegalStateException("The planningWindowStart ("
                            + planningWindowStart + ") must be in the shiftDateList ("
                            + shiftDateList +").");
                }
                ShiftDate oldLastShiftDate = shiftDateList.get(shiftDateList.size() - 1);
                ShiftDate newShiftDate = new ShiftDate();
                newShiftDate.setId(oldLastShiftDate.getId() + 1L);
                newShiftDate.setDayIndex(oldLastShiftDate.getDayIndex() + 1);
                newShiftDate.setDateString(oldLastShiftDate.determineNextDateString());
                newShiftDate.setDayOfWeek(oldLastShiftDate.getDayOfWeek().determineNextDayOfWeek());
                List<Shift> refShiftList = planningWindowStart.getShiftList();
                List<Shift> newShiftList = new ArrayList<Shift>(refShiftList.size());
                newShiftDate.setShiftList(newShiftList);
                nurseRoster.getShiftDateList().add(newShiftDate);
                scoreDirector.afterProblemFactAdded(newShiftDate);
                Shift oldLastShift = nurseRoster.getShiftList().get(nurseRoster.getShiftList().size() - 1);
                long shiftId = oldLastShift.getId() + 1L;
                int shiftIndex = oldLastShift.getIndex() + 1;
                long shiftAssignmentId = nurseRoster.getShiftAssignmentList().get(
                        nurseRoster.getShiftAssignmentList().size() - 1).getId() + 1L;
                for (Shift refShift : refShiftList) {
                    Shift newShift = new Shift();
                    newShift.setId(shiftId);
                    shiftId++;
                    newShift.setShiftDate(newShiftDate);
                    newShift.setShiftType(refShift.getShiftType());
                    newShift.setIndex(shiftIndex);
                    shiftIndex++;
                    newShift.setRequiredEmployeeSize(refShift.getRequiredEmployeeSize());
                    newShiftList.add(newShift);
                    nurseRoster.getShiftList().add(newShift);
                    scoreDirector.afterProblemFactAdded(newShift);
                    for (int indexInShift = 0; indexInShift < newShift.getRequiredEmployeeSize(); indexInShift++) {
                        ShiftAssignment newShiftAssignment = new ShiftAssignment();
                        newShiftAssignment.setId(shiftAssignmentId);
                        shiftAssignmentId++;
                        newShiftAssignment.setShift(newShift);
                        newShiftAssignment.setIndexInShift(indexInShift);
                        nurseRoster.getShiftAssignmentList().add(newShiftAssignment);
                        scoreDirector.afterEntityAdded(newShiftAssignment);
                    }
                }
                windowStartIndex++;
                ShiftDate newPlanningWindowStart = shiftDateList.get(windowStartIndex);
                nurseRosterParametrization.setPlanningWindowStart(newPlanningWindowStart);
                nurseRosterParametrization.setLastShiftDate(newShiftDate);
                scoreDirector.afterProblemFactChanged(nurseRosterParametrization);
            }
        }, true);
    }

    public void deleteEmployee(final Employee employee) {
        logger.info("Scheduling delete of employee ({}).", employee);
        doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                NurseRoster nurseRoster = (NurseRoster) scoreDirector.getWorkingSolution();
                // First remove the problem fact from all planning entities that use it
                for (ShiftAssignment shiftAssignment : nurseRoster.getShiftAssignmentList()) {
                    if (ObjectUtils.equals(shiftAssignment.getEmployee(), employee)) {
                        scoreDirector.beforeVariableChanged(shiftAssignment, "employee");
                        shiftAssignment.setEmployee(null);
                        scoreDirector.afterVariableChanged(shiftAssignment, "employee");
                    }
                }
                scoreDirector.triggerVariableListeners();
                // A SolutionCloner does not clone problem fact lists (such as employeeList)
                // Shallow clone the employeeList so only workingSolution is affected, not bestSolution or guiSolution
                nurseRoster.setEmployeeList(new ArrayList<Employee>(nurseRoster.getEmployeeList()));
                // Remove it the problem fact itself
                for (Iterator<Employee> it = nurseRoster.getEmployeeList().iterator(); it.hasNext(); ) {
                    Employee workingEmployee = it.next();
                    if (ObjectUtils.equals(workingEmployee, employee)) {
                        scoreDirector.beforeProblemFactRemoved(workingEmployee);
                        it.remove(); // remove from list
                        scoreDirector.beforeProblemFactRemoved(employee);
                        break;
                    }
                }
            }
        });
    }

    public void moveShiftAssignmentToEmployee(ShiftAssignment shiftAssignment, Employee toEmployee) {
        solutionBusiness.doChangeMove(shiftAssignment, "employee", toEmployee);
        solverAndPersistenceFrame.resetScreen();
    }

}
