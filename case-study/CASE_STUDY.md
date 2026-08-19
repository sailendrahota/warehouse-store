# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
I would first clarify the source of each cost and how it should be allocated between Warehouses and Stores. Important considerations are direct vs. shared costs, allocation rules, time periods, data accuracy, and historical tracking. I would also ask how transportation, labor, and overhead costs are calculated and which system is the source of truth for each cost.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
I would use operational and cost data to identify areas such as low warehouse utilization, high transportation costs, excess inventory, or inefficient processes. I would prioritize opportunities based on expected savings, implementation effort, risk, and impact on service quality. I would start with a pilot, measure the results, and then scale successful changes.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
Integration provides a single and reliable view of costs and improves reporting and decision making. I would first define which system is the source of truth for each type of data. The integration should consider APIs/events, data validation, idempotency, retries, error handling, and reconciliation to avoid missing or duplicated financial data.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
I would use historical costs together with operational drivers such as demand, seasonality, warehouse capacity, labor, and transportation costs. The system should support budgets, forecasts, actual costs, and variance analysis. I would also consider versioning and auditability so that changes to budgets and forecasts can be tracked.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
The old Warehouse should be archived rather than overwritten so its historical costs remain traceable. The new Warehouse can reuse the Business Unit Code while having its own lifecycle and cost history. I would compare the new Warehouse's expected costs with the historical costs and approved budget, while also defining effective dates to avoid double-counting costs during the transition.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.s