import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import Categories from '../../components/categories'

export type CategoryActions = {
  addCategory: (name: string) => void
  updateCategory: (payload: { index: number, value: string }) => void
  renameCategory: (payload: { index: number, name: string }) => void
  deleteCategory: (index: number) => void
  selectCategory: (index: number) => void
}

export type Category = {
  name: string
  value: string
}

export type CategoriesState = {
  all: Category[]
  selected?: number
}

const { actions, reducer } = createSlice({
  name: 'categories',
  initialState: { all: [] } as CategoriesState,
  reducers: {
    addCategory(state, action: PayloadAction<string>) {
      const name = action.payload.trim()
      if (name !== '') {
        const oldCategories = state.all
        const category = {
          name,
          value: ''
        }
        const all = [...oldCategories, category]
        const selected = all.length - 1
        return {
          all,
          selected
        }
      } else {
        return state
      }
    },
    updateCategory(state, action: PayloadAction<{ index: number, value: string }>) {
      const { all: oldCategories } = state
      const { index, value } = action.payload
      const { name } = state.all[index]
      const all = [...oldCategories.slice(0, index), { name, value }, ...oldCategories.slice(index + 1)]
      return {
        ...state,
        all
      }
    },
    renameCategory(state, action: PayloadAction<{ index: number, name: string }>) {
      const { all: oldCategories } = state
      const { index, name } = action.payload
      const { value } = state.all[index]
      const all = [...oldCategories.slice(0, index), { name, value }, ...oldCategories.slice(index + 1)]
      return {
        ...state,
        all
      }
    },
    deleteCategory(state, action: PayloadAction<number>) {
      const { all: oldCategories, selected: oldSelected } = state
      const index = action.payload
      const all = [...oldCategories.slice(0, index), ...oldCategories.slice(index + 1)]
      const selected = Math.min(oldSelected?oldSelected: Number.MAX_SAFE_INTEGER, all.length-1)
      return { all, selected }
    },
    selectCategory(state, action: PayloadAction<number>) {
      const selected = action.payload
      return {
        ...state,
        selected
      }
    },
  }
})

export const { addCategory, updateCategory, renameCategory, deleteCategory, selectCategory } = actions

export default reducer