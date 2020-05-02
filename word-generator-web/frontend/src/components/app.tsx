import React from 'react';
import { connect } from 'react-redux'
import './app.css';
import { updateCommand } from '../redux/slices/command-slice';
import { AppState } from '../redux/app-state';
import Header from './header';
import Main from './main';
import Footer from './footer';
import generateWord from '../redux/async/generateWord'

type MappedProps = AppState
type MappedDispatch = {
  onType: (event: React.ChangeEvent<HTMLInputElement>) => void,
  generateWord: ()=> void
}
type Props = MappedProps & MappedDispatch

export function App(props: Props) {
  const { command, onType, generateWord } = props
  return (
    <div className="app">
      <Header command={command} onType={onType} generateWord={generateWord} />
      <Main />
      <Footer />
    </div>
  )
}

//<DebounceInput
// value={command}
// debounceTimeout={500}
// onChange={props.onType} />

const mapStateToProps = (state: AppState): MappedProps => state

const mapDispatchToProps = (dispatch: any): MappedDispatch => ({
  onType: (event: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(updateCommand((event.target as HTMLInputElement).value))
  },
  generateWord: () => dispatch(generateWord())
})

export default connect(mapStateToProps, mapDispatchToProps)(App);