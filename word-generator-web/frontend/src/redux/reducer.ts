import { combineReducers } from 'redux'
import command from './slices/command-slice'
import words from './slices/words-slice'
import requests from './slices/requests-slice'
import { persistReducer } from 'redux-persist'
import storage from 'redux-persist/lib/storage'

const rootReducer = combineReducers({
  command,
  words,
  requests
})

const persistConfig = {
  key: 'root',
  storage,
  blacklist: ['requests']
}

export default persistReducer(persistConfig, rootReducer)