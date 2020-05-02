import { combineReducers } from 'redux'
import command from './slices/command-slice'
import results from './slices/result-slice'
import { persistReducer } from 'redux-persist'
import storage from 'redux-persist/lib/storage'

const rootReducer = combineReducers({
  command,
  results
})

const persistConfig = {
  key: 'root',
  storage,
}

export default persistReducer(persistConfig, rootReducer)