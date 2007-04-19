require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class PositionTest < MarketceteraTestBase
  fixtures :currencies


  def test_get_top_positioned_accounts
    create_trades_in_account(3, "TOLI")
    create_trades_in_account(5, "RAMA")
    create_trades_in_account(2, "LENA")
    create_trades_in_account(3, "MOLI")

    accounts = Position.get_top_positioned_accounts(3)
    assert_equal 3, accounts.length
    assert_equal ["RAMA", 5], [accounts[0][0].nickname, accounts[0][1]]
    assert_equal ["TOLI", 3], [accounts[1][0].nickname, accounts[1][1]]
    assert_equal ["MOLI", 3], [accounts[2][0].nickname, accounts[2][1]]
  end
  
  def test_get_positions_as_of_inclusive_date_and_account
    create_trades_in_account(3, "vasya")
    create_trades_in_account(2, "vasya", Date.today-1)
    create_trades_in_account(3, "bob")
  
    assert_equal 3, Position.get_positions_as_of_inclusive_date_and_account(Date.today, Account.find_by_nickname("vasya")).length
    assert_equal 3, Position.get_positions_as_of_inclusive_date_and_account(Date.today+1,  Account.find_by_nickname("vasya")).length
    assert_equal 0, Position.get_positions_as_of_inclusive_date_and_account(Date.today,  Account.find_by_nickname("noName")).length
    assert_equal 2, Position.get_positions_as_of_inclusive_date_and_account(Date.today-1,  Account.find_by_nickname("vasya")).length
  end
  
  private
  def create_trades_in_account(num_trades, account, date=Date.today)
    for i in 1..num_trades
      create_test_trade(100, 20.11, Side::QF_SIDE_CODE[:buy], account, date, "IFLI-"+i.to_s, 4.99, "USD")
    end
  end  
end
