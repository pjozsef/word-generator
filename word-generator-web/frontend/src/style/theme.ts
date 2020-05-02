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
        main: "#f2a365"
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