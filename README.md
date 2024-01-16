# Example of usage openAI language model

## Build

Command line prompt:
```
./gradlew clean build
```
## Run
```
./gradlew bootRun -DAPI_KEY={your-openai-api-key}
```

Running on http://localhost:8080
### Using docker

- Install docker CLI using original script from [official site](https://get.docker.com/)

- Run following cmd prompts:
```
docker build -t demo-openai .
```
```
 docker run -p 80:8080 -e API_KEY={your-openai-api-key} --rm -it demo-openai
```
Running on http://localhost
- Original docker registry repository [dimayeshenko](https://hub.docker.com/r/dimayeshenko/demo-openai)