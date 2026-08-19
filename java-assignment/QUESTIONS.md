# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```
Currently, there are different styles. For example, the Store side uses Panache's active-record style directly from the entity,.
I would standardize toward repository/port-based persistence for domain-heavy areas, but migrate incrementally because consistency and business value are more important than rewriting working CRUD code."

```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```
I prefer :- For a public API, cross-team API, or API consumed by multiple clients, I would generally prefer OpenAPI-first.
For a small internal service, handwritten endpoints can be perfectly acceptable.

the API contract becomes explicit and can be shared between teams. It also reduces inconsistencies in endpoint definitions, request/response models, documentation, and client generation.
 
 The downside is that generated code can introduce additional build complexity and sometimes makes customization less convenient. Developers also need to understand what is generated and what should actually be modified.

With a handwritten API, developers have complete control and the implementation is straightforward, which can be simpler for a small internal service. But over time, API contracts can drift from implementation and documentation can become inconsistent.

For this project, since Warehouse already uses OpenAPI generation, I would probably standardize the API layer around OpenAPI, especially if the organization expects multiple consumers or generated clients.

I would keep business logic completely outside the generated interface:
 
 
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```
I don't optimize for coverage percentage alone. I prioritize business-critical rules, transaction boundaries, and failure scenarios, then use integration and a small number of API tests to verify that the layers work together

```