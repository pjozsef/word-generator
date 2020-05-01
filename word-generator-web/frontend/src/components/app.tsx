import React, { Dispatch } from 'react';
import { connect } from 'react-redux'
import './app.css';
import { updateCommand } from '../redux/slices/command-slice';
import { AppState } from '../redux/app-state';
import { DebounceInput } from 'react-debounce-input';

type MappedProps = AppState
type MappedDispatch = {
  onType: (event: React.FormEvent<HTMLInputElement>) => void
}
type Props = MappedProps & MappedDispatch

function App(props: Props) {
  return (
    <DebounceInput
            value={''}
            debounceTimeout={500}
            onChange={props.onType} />
  );
}

const mapStateToProps = (state: AppState): MappedProps => state

const mapDispatchToProps = (dispatch: any): MappedDispatch => ({
  onType: (event: React.FormEvent<HTMLInputElement>) => {
    dispatch(updateCommand((event.target as HTMLInputElement).value))
  }
})

export default connect(mapStateToProps, mapDispatchToProps)(App);
