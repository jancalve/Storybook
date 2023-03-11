# Storybook

## Description
Given a user-defined plot Storybook will create a video with a story made up by ChatGPT, imagery from DALL-E 2 and voiceover from Amazon Polly.
#
Medium article: https://link.medium.com/vBNbQ7jn5xb

#
## Requirements
Java 17

FFMPEG (brew install ffmpeg)

AWS CLI (brew install awscli)

OpenAI API token

AWS credentials


#
## Usage
gradle jar

java -jar build/libs/storybook-1.0-SNAPSHOT.jar openai-api-key "your-plot-here"  

After running, the video will be present in the folder named "generated".

#
## NB!
This project is a very simple POC demonstrating how different AI services can be composed together. 

The APIs used are NOT stable yet and might break at any moment.
