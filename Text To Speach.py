"""def synthesize_text(text):
    Synthesizes speech from the input string of text.
    from google.cloud import texttospeech
    client = texttospeech.TextToSpeechClient()

    input_text = texttospeech.types.SynthesisInput(text="هذا صحيح")

    # Note: the voice can also be specified by name.
    # Names of voices can be retrieved with client.list_voices().
    voice = texttospeech.types.VoiceSelectionParams(
        language_code='ar-EG',
        ssml_gender=texttospeech.enums.SsmlVoiceGender.FEMALE)

    audio_config = texttospeech.types.AudioConfig(
        audio_encoding=texttospeech.enums.AudioEncoding.MP3)

    response = client.synthesize_speech(input_text, voice, audio_config)

    # The response's audio_content is binary.
    with open('output.wav', 'wb') as out:
        out.write(response.audio_content)
        print('Audio content written to file "output.mp3"')
"""
from gtts import gTTS

def tts(filename,text,lang):
    gTTS(text=text,lang= lang).save(filename)
    print("Done")



text = "نعناعةْ"
name = "نعناعة"
lang = "ar"
tts(name,text,lang)