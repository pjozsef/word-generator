import { Action } from 'redux'
import { ThunkAction } from 'redux-thunk'
import { AppState } from '../app-state';
import { newResult } from '../slices/result-slice';

export default (): ThunkAction<void, AppState, unknown, Action<string>> => dispatch => {
    dispatch(
        newResult([new Date().getTime() + ''])
    )
}
