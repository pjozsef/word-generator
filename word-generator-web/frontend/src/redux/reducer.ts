import { combineReducers } from 'redux'
import command from './slices/command-slice'
import words from './slices/words-slice'
import requests from './slices/requests-slice'
import categories from './slices/categories-slice'
import { persistReducer } from 'redux-persist'
import storage from 'redux-persist/lib/storage'

const rootReducer = combineReducers({
  command,
  words,
  requests,
  categories
})

const persistConfig = {
  key: 'root',
  storage,
  blacklist: ['requests']
}

export default persistReducer(persistConfig, rootReducer)