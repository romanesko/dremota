<div class="col-12">

  <div class="card mb-3">
    <div class="table-responsive">
      <table class="table table-vcenter card-table table-bordered price-table">
        <thead>
        <tr>
          <th class="w-1 text-center">вкл</th>
          <th>Название</th>
          <th class="w-1 text-center">Сумма</th>
          <th class="w-1 text-center">Валюта</th>
          <th class="w-1 text-center">Дней</th>
          <th class="w-1 text-center">Del</th>

        </tr>
        </thead>
        <tbody>
        {#if price}
          {#each price as p}

            <tr>
              <td class="text-center">
                <input
                    class="form-check-input m-0 align-middle"
                    bind:checked={p.enabled}
                    type="checkbox"
                    aria-label="Select invoice">
              </td>
              <td>
                <input
                    type="text"
                    bind:value={p.title}
                    class="no-border form-control form-control"
                    aria-label="Search invoice">
              </td>
              <td class="text-secondary">
                <input
                    type="number"
                    bind:value={p.amount}
                    class="num-field  no-border form-control form-control text-center"
                    aria-label="Search invoice">
              </td>
              <td class="text-secondary text-center">
                {p.currency}

              </td>
              <td class="text-secondary">
                <input
                    type="number"
                    bind:value={p.days}
                    class="num-field  no-border form-control form-control text-center"
                    aria-label="Search invoice">
              </td>
              <td class="text-secondary text-center">
                <button class="btn btn-sm btn-danger" onclick={()=>deleteItem(p)}>
                  <img src={trash} alt="trash" style="width: 1rem; height: 1rem; color:white"/>
                </button>
              </td>

            </tr>
          {/each}
        {/if}

        </tbody>
      </table>
    </div>
  </div>

</div>

<div class="col-12">
  <div class="d-flex justify-content-between">
    <button class="btn btn-sm btn-outline-primary" onclick={addPrice}>+ Добавить строку</button>
  </div>
</div>

{#if price}
  <div class="col-12">
    <div class="d-flex justify-content-end">
      <button class="btn btn-primary" onclick={saveItems}>Cохранить</button>
    </div>
  </div>
{/if}


<script lang="ts">

    import api from "@/lib/api";
    import type {Price} from "@/models.js";
    import trash from '@/assets/trash.svg';
    import {alertError, alertSuccess, log} from "@/lib/common";

    let price: Price[] | undefined = $state(undefined)

  function getPrice() {
    api.getPrice().then(p => {
      price = p
    })
  }

  getPrice()

  const itemsToDelete: Price[] = []

  function deleteItem(item: Price) {
    if (!price) {
      return
    }
    log('deleteItem', item)
    if (item.id > 0) {

      itemsToDelete.push(item)
    }
    price = price.filter(i => i !== item)
  }

  function saveItems() {
    if (!price) {
      return
    }

    const promises = []


    for (const item of itemsToDelete) {
      promises.push(api.deletePrice(item))
    }
    for (const item of price) {

      if (item.title.length === 0) {
        alertError('Название не может быть пустым')
        return
      }
      if (item.amount <= 0) {
        alertError('Сумма не может быть меньше или равна 0')
        return
      }
      if (item.currency.length === 0) {
        alertError('Валюта не может быть пустой')
        return
      }
      if (item.days <= 0) {
        alertError('Дни не могут быть меньше или равны 0')
        return
      }


      promises.push(api.updatePrice(item))
    }
    itemsToDelete.length = 0
    Promise.all(promises)
        .catch((err) => {
          alertError(err)
        })
        .then((a: any) => {
          if (a) {
            alertSuccess('Изменения сохранены')

          }
        }).finally(() => {
      getPrice()
    })

  }


  function addPrice() {
    if (!price) {
      return
    }

    const minId = price.length ? Math.min(...price.map(p => p.id)) : 0;

    const p = {id: minId - 1, title: '', amount: 0, currency: 'XTR', days: 0, enabled: true, createdAt: ''} as Price

    price = [...price, p]
  }


</script>

<style>
  .price-table tr td {
    padding: 0;
  }

  .price-table tr td input.no-border {
    /*padding: 10px;*/
    border: none;
    -webkit-tap-highlight-color: transparent;

  }

  .price-table tr td input.num-field {
    min-width: 60px;
    margin-left: 10px;
  }


  .price-table tr td input:focus {
    outline: none !important;
    box-shadow: none;
  }

</style>
