async function main () {
  try {
    const buttonStart = document.querySelector('#buttonStart')
    const buttonStop = document.querySelector('#buttonStop')
    const buttonSend = document.querySelector('#buttonSend')
    const uploadFileForm = document.querySelector('#uploadFileForm')
    const audio = document.querySelector('#audio')
    let blob

    const stream = await navigator.mediaDevices.getUserMedia({ // <1>
      video: false,
      audio: true,
    })

    const [track] = stream.getAudioTracks()
    const settings = track.getSettings() // <2>

    const audioContext = new AudioContext()
    await audioContext.audioWorklet.addModule('js/audio-recorder.js') // <3>

    const mediaStreamSource = audioContext.createMediaStreamSource(stream) // <4>
    const audioRecorder = new AudioWorkletNode(audioContext, 'audio-recorder') // <5>
    const buffers = []

    audioRecorder.port.addEventListener('message', event => { // <6>
      buffers.push(event.data.buffer)
    })
    audioRecorder.port.start() // <7>

    mediaStreamSource.connect(audioRecorder) // <8>
    audioRecorder.connect(audioContext.destination)

    buttonStart.addEventListener('click', event => {
      buttonStart.setAttribute('disabled', 'disabled')
      buttonSend.setAttribute('disabled', 'disabled')
      buttonStop.removeAttribute('disabled')

      const parameter = audioRecorder.parameters.get('isRecording')
      parameter.setValueAtTime(1, audioContext.currentTime) // <9>

      buffers.splice(0, buffers.length)
    })

    buttonStop.addEventListener('click', event => {
      buttonStop.setAttribute('disabled', 'disabled')
      buttonStart.removeAttribute('disabled')
      buttonSend.removeAttribute('disabled')

      const parameter = audioRecorder.parameters.get('isRecording')
      parameter.setValueAtTime(0, audioContext.currentTime) // <10>

      blob = encodeAudio(buffers, settings) // <11>
      const url = URL.createObjectURL(blob)

      audio.src = url
    })

    buttonSend.addEventListener('click', event => {
      upload(blob)
    })
    uploadFileForm.addEventListener('submit',  event => {
      event.preventDefault();
      uploadFile()
    })
  } catch (err) {
    console.error(err)
  }
}

main()