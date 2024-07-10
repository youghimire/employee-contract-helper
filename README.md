# employee-contract-helper
An application for helping employer crate a new contract with the help of Gen AI

Basic functionality of the agent
- Save user conversation into multiple session
- Each session further divided into multiple sections based on the API call, currently there are two module i.e. General information
    and employee information
- Can switch between LLM provider programmatically from LLMHandler
- External function calling by processing the response JSON from LLM
- I tested with Hugging Face chat completion API and Running Model locally using LM Studio and using chat completion API
- Messages are stored in Postgres DB
- Validating the result returned from LLM before posting to external API
- Forcing LLM by adding prompt to user message if the conversation goes longer then expected for each section

To Run
- Need to copy application.properties.example to application.properties with necessary secret and token
- Currently, it's using Hugging face chat completion API. Token can be found from https://huggingface.co/settings/tokens
- To Run this Application we need a Postgres server need to create a user and database and update that on properties file
- now need to add the External API URL on properties file
- Simply running with mvn command will work with JDK 17 or above provided or we can run by Dockerfile

Service classes
- AgentController : Responsible of handling user request forwarding to different services and finally returning the appropriate result
- LLMHandler : Responsible for switching LLM provider parsing the response from LLM and if there are errors then re-calling the model
- ContractService : Responsible for calling the contract creation API and update API and parsing the response
- ValidateInformation: To validate information returned from LLM and to parse the json and map country iso to country
- Message and Session service : provide CRUD operation to repository
- HuggingFace and OpenAPI integrations
- Instructions are on two files at src/main/resources for two sections




![alt text](https://github.com/youghimire/employee-contract-helper/blob/main/Agent_eor.jpg)
