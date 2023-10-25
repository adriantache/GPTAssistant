# GPTAssistant
Just a simple app to access ChatGPT via an Android app, with voice input and output.

Requires an [OpenAI api key](https://platform.openai.com/account/api-keys) to work, which you need to add to your local.properties file. 
Does not require a premium ChatGPT account, but does offer access to the latest model available in the API, which is currently GPT4-0613.

The app supports both voice and text input, and a number of QOL features like quick search with Google, easily copying input etc. 

It also supports conversation mode, which enables you to only use voice to have a full conversation with ChatGPT. Eventually, this will 
integrate with the widget in order to allow you to not even open the app to ask questions, while still having them be automatically saved
for future reference.

**Pending features:**
- widget support
- support for editing prompt (in case voice input was misheard)
- improve Firebase database implementation (and ideally switch to something more complex that allows for pagination)
- architecture rewrite to support states
- Firebase login implementation to persist conversations across installs (and encrypted OpenAI API key, if manually input)
- UI to manually input OpenAI API key on startup and logic to persist it
- (eventually) much nicer design
- (eventually) nicer formatting for ChatGPT output
