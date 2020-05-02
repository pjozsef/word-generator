import {
    createMuiTheme,
  } from '@material-ui/core/styles';
  
  export default createMuiTheme({
    palette: {
      type: "dark",
      primary: {
          main: "#30475e"
      },
      secondary: {
        main: "#23b39a"
      },
      error: {
        main: "#B00020"
      },
      background: {
        default: "#0f1317",
        paper: "#ececec"
      }
    }
  });