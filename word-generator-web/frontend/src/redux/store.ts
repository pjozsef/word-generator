import { configureStore } from '@reduxjs/toolkit'
import rootReducer from './reducer'
import { persistStore } from 'redux-persist'

const store = configureStore({
  reducer: rootReducer,
})

export const persistor = persistStore(store)

export type AppDispatch = typeof store.dispatch

export default store