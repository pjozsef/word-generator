export default (action: () => void) => {
    return (event: React.KeyboardEvent) => {
      if (event.nativeEvent.keyCode === 13) {
        action()
      }
    }
  }