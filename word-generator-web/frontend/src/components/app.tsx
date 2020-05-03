import React from 'react';
import { connect } from 'react-redux'
import './app.css';
import { updateCommand } from '../redux/slices/command-slice';
import { AppState } from '../redux/app-state';
import Header from './header';
import Main from './main';
import Footer from './footer';
import generateWord from '../redux/async/generate-word';

type MappedProps = AppState
type MappedDispatch = {
  onType: (event: React.ChangeEvent<HTMLInputElement>) => void,
  generateWord: ()=> void
}
type Props = MappedProps & MappedDispatch

export function App(props: Props) {
  const { command, onType, generateWord, requests, words } = props
  const { generate } = requests
  return (
    <div className="app">
      <Header command={command} onType={onType} generateWord={generateWord} isFetching={generate} />
      <Main words={words}/>
      <Footer />
    </div>
  )
}

const mapStateToProps = (state: AppState): MappedProps => state

const mapDispatchToProps = (dispatch: any): MappedDispatch => ({
  onType: (event: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(updateCommand((event.target as HTMLInputElement).value))
  },
  generateWord: () => dispatch(generateWord())
})

export default connect(mapStateToProps, mapDispatchToProps)(App);
